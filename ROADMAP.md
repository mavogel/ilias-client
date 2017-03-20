Roadmap
=======

## Features:
* Change detector to verify the expected structure of the result XMLs.
* Change max members allowed in a group.

## Improvements:
### MUST
1. If user is not admin in any of his courses, display an explanation dialog how to fix it in Ilias.
2. Make templates more customizable from cmd line input (columns and name of columns).
3. Groups for a course should be cached.
4. Handle whitespaces in user input.

### SHOULD
1. shutdown-hook is not triggered on Windows for CTRL+C.
2. Test the statemachine with integration tests.
3. Using jline for cmd line autocomplete.
4. Check this [.travis.yml](https://github.com/OpenFeign/feign/blob/master/.travis.yml) for releasing.

### COULD
1. Run Bulk calls to Ilias in parallel.
2. Add `guice` DI library.
3. Revise `util` from google guava (for gh docs).
4. Evaluate `feign` for HTTP client
5. add checkstyle.xml
