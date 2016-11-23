Roadmap
=======

## Features:
* Change detector to verify the expected structure of the result XMLs.

## Improvements:
### MUST
1. If user is not admin in any of his courses, display an explanation dialog how to fix it in Ilias.
2. Make templates more customizable from cmd line input (columns and name of columns).
3. Groups for a course should be cached.

### SHOULD
1. Test the statemachine with integration tests.
2. Using jline for cmd line autocomplete.
3. Revise `util` from google guava (for gh docs).

### COULD
1. shutdown-hook is not triggered on Windows for CTRL+C.
2. Run Bulk calls to Ilias in parallel.
3. Add `guice` DI library.
