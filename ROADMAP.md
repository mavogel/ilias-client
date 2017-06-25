Roadmap
=======

## Features:
* Change detector to verify the expected structure of the result XMLs.

## Improvements:
### MUST
1. If user is not admin in any of his courses, display an explanation dialog how to fix it in Ilias.
1. Make templates more customizable from cmd line input (columns and name of columns).
1. Groups for a course should be cached.
1. Repeating actions on groups (new transition)

### SHOULD
1. shutdown-hook is not triggered on Windows for CTRL+C.
1. Using jline for cmd line autocomplete.
1. Check this [.travis.yml](https://github.com/OpenFeign/feign/blob/master/.travis.yml) for releasing.

### COULD
1. Run Bulk calls to Ilias in parallel.
1. Add `guice` DI library.
1. Revise `util` from google guava (for gh docs).
1. Evaluate `feign` for HTTP client
1. add checkstyle.xml
1. Release it as a docker container.
