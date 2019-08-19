# This file patches few methods in webrick to make it work in
# TruffleRuby's single threaded mode.
# Original code creating extra threads is commented out

require 'webrick'
require 'webrick/server'

module WEBrick
  class GenericServer
    def start(&block)
      raise ServerError, "already started." if @status != :Stop
      server_type = @config[:ServerType] || SimpleServer

      setup_shutdown_pipe

      server_type.start {
        @logger.info \
          "#{self.class}#start: pid=#{$$} port=#{@config[:Port]}"
        @status = :Running
        call_callback(:StartCallback)

        shutdown_pipe = @shutdown_pipe

        # thgroup = ThreadGroup.new
        begin
          while @status == :Running
            begin
              sp = shutdown_pipe[0]
              if svrs = IO.select([sp, *@listeners])
                if svrs[0].include? sp
                  # swallow shutdown pipe
                  buf = String.new
                  nil while String ===
                      sp.read_nonblock([sp.nread, 8].max, buf, exception: false)
                  break
                end
                svrs[0].each { |svr|
                  @tokens.pop # blocks while no token is there.
                  if sock = accept_client(svr)
                    unless config[:DoNotReverseLookup].nil?
                      sock.do_not_reverse_lookup = !!config[:DoNotReverseLookup]
                    end
                    start_thread(sock, &block)
                    # th[:WEBrickThread] = true
                    # thgroup.add(th)
                  else
                    @tokens.push(nil)
                  end
                }
              end
            rescue Errno::EBADF, Errno::ENOTSOCK, IOError => ex
              # if the listening socket was closed in GenericServer#shutdown,
              # IO::select raise it.
            rescue StandardError => ex
              msg = "#{ex.class}: #{ex.message}\n\t#{ex.backtrace[0]}"
              @logger.error msg
            rescue Exception => ex
              @logger.fatal ex
              raise
            end
          end
        ensure
          cleanup_shutdown_pipe(shutdown_pipe)
          cleanup_listener
          @status = :Shutdown
          @logger.info "going to shutdown ..."
          # thgroup.list.each{|th| th.join if th[:WEBrickThread] }
          call_callback(:StopCallback)
          @logger.info "#{self.class}#start done."
          @status = :Stop
        end
      }
    end

    def start_thread(sock, &block)
      # Thread.start {
      begin
        Thread.current[:WEBrickSocket] = sock
        begin
          addr = sock.peeraddr
          @logger.debug "accept: #{addr[3]}:#{addr[1]}"
        rescue SocketError
          @logger.debug "accept: <address unknown>"
          raise
        end
        if sock.respond_to?(:sync_close=) && @config[:SSLStartImmediately]
          # WEBrick::Utils.timeout(@config[:RequestTimeout]) do
          begin
            sock.accept # OpenSSL::SSL::SSLSocket#accept
          rescue Errno::ECONNRESET, Errno::ECONNABORTED,
              Errno::EPROTO, Errno::EINVAL
            Thread.exit
          end
          # end
        end
        call_callback(:AcceptCallback, sock)
        block ? block.call(sock) : run(sock)
      rescue Errno::ENOTCONN
        @logger.debug "Errno::ENOTCONN raised"
      rescue ServerError => ex
        msg = "#{ex.class}: #{ex.message}\n\t#{ex.backtrace[0]}"
        @logger.error msg
      rescue Exception => ex
        @logger.error ex
      ensure
        @tokens.push(nil)
        Thread.current[:WEBrickSocket] = nil
        if addr
          @logger.debug "close: #{addr[3]}:#{addr[1]}"
        else
          @logger.debug "close: <address unknown>"
        end
        sock.close
      end
      Thread.current
      # }
    end
  end

  module Config
    HTTP[:RequestTimeout] = 2
  end

  require 'webrick/utils'

  module Utils
    def timeout(seconds, exception = Timeout::Error)
      return yield
    end
  end
end

module Timeout
  def timeout(sec, exception = Error)
    yield sec
  end
  module_function :timeout
end
