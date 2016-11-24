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
1. shutdown-hook is not triggered on Windows for CTRL+C.
2. Test the statemachine with integration tests.
3. Using jline for cmd line autocomplete.

### COULD
1. Run Bulk calls to Ilias in parallel.
2. Add `guice` DI library.
3. Revise `util` from google guava (for gh docs).
