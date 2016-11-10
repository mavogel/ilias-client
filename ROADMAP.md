Roadmap
=======

# Features:
* Change detector to verify the expected structure of the result XMLs.

# Improvements:
## MUST
1. If user is not admin in any of his courses, display an explanation dialog how to fix it in Ilias.
2. Make templates more customizable from cmd line input (columns and name of columns).
3. Groups for a course should be cached.

## SHOULD
1. Add Travis CI for deploying to maven central.
  - Get [Sonatype](http://central.sonatype.org/pages/ossrh-guide.html) OSS account.
  - Add the following [instructions](https://gist.github.com/letmaik/4060735) to the `.travis.yml`
2. Using jline for cmd line autocomplete.

## COULD
1. shutdown-hook is not triggered on Windows for CTRL+C.