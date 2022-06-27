require 'pp'
require 'sinatra/base'

# noinspection RubyConstantNamingConvention,RubyParenthesesAfterMethodCallInspection,RubyUnlessWithElseInspection
class PolyglotApp < Sinatra::Base

  enable :static
  set :server, 'webrick'
  disable :logging

  PEOPLE_DB = []

  def self.initialize_db
    PEOPLE_DB.clear
    PEOPLE_DB.push JsPerson.new('John', 'Doe', 50)
  end

  JS = Polyglot::InnerContext.new
  JS.eval('js', 'file => load(file)').call("#{__dir__}/public/person.js")
  JsPerson = JS.eval('js', 'Person')
  initialize_db

  def js_person_as_hash(js_user)
    { firstName: js_user[:firstName],
      lastName:  js_user[:lastName],
      age:       js_user[:age] }
  end

  get '/' do
    File.read(File.join(self.class.public_folder, 'index.html'))
  end

  get '/people.json' do
    data = PEOPLE_DB.map do |js_user|
      js_person_as_hash(js_user)
    end
    data.to_json
  end

  post '/person.json' do
    request.body.rewind # in case someone already read it
    person = JSON.parse request.body.read

    js_person = JsPerson.new(person['firstName'], person['lastName'], person['age'])
    js_person_is_invalid = js_person.isInvalid()
    if js_person_is_invalid
      [403, 'Invalid user.']
    else
      PEOPLE_DB.push js_person
      [200, 'Ok']
    end
  end

  get '/add/:times' do |times|
    times.to_i.times do |i|
      PEOPLE_DB.push JsPerson.new("John#{i}", "Doe#{i}", i % 120 + 1)
    end
    redirect '/'
  end

  get '/clean' do
    self.class.initialize_db
    redirect '/'
  end

  get '/exit' do
    PolyglotApp.quit!
    redirect '/'
  end
end

PolyglotApp.run!
