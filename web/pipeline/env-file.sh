# env-file.sh
# utility to load an environment file into a bash shell
# usage:
# ENV_FILE=<path> source env-file.sh

test "$ENV_FILE" || { printf 'env-file.sh: missing required input: ENV_FILE\n' >&2; return 1; }

while read envdef; do
  if [[ $envdef =~ ^([[:alnum:]_]+)=(.*)$ ]]; then
    declare -x "$envdef"
  else
    continue
  fi
done < "$ENV_FILE"
