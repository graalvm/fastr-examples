/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This file is made available under version 3 of the GNU General Public License.
 */

#include <stdio.h>
#include <stdlib.h>
#include "graal_isolate.h"
#include "libhellojacoco.h"

int main() {

   graal_isolatethread_t * graal_isolate_thread;
   graal_isolate_t * isolate;

   /* Execute false branch within the invoked java method. */
   graal_create_isolate(NULL, &isolate, NULL);
   graal_attach_thread(isolate, &graal_isolate_thread);
   isolate_test(graal_isolate_thread, 0);
   /* Profiles are dumped during shutdown. */
   tear_down(graal_isolate_thread);
   graal_tear_down_isolate(graal_isolate_thread);

   /* Execute true branch within the invoked java method from another isolate. */
   graal_create_isolate(NULL, &isolate, NULL);
   graal_attach_thread(isolate, &graal_isolate_thread);
   isolate_test(graal_isolate_thread, 1);
   /* Profiles are dumped during shutdown. */
   tear_down(graal_isolate_thread);
   graal_tear_down_isolate(graal_isolate_thread);
   return 0;
}
