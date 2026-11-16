#!/bin/bash
#
# Run by the JVM through -XX:OnOutOfMemoryError (see queue_runner.sh). Writes a system errors
# report recording the OutOfMemoryError, in the format the validator uses, so that the
# acceptance test comparator reports the dataset, since the JVM then exits
# (-XX:+ExitOnOutOfMemoryError) before the validator writes its own reports.
#
# Usage: write_oom_system_errors.sh <path of the system errors report>

errors_file="$1"
mkdir -p "$(dirname "$errors_file")"
cat > "$errors_file" <<'EOF'
{
  "notices": [
    {
      "code": "thread_execution_error",
      "severity": "ERROR",
      "totalNotices": 1,
      "sampleNotices": [
        {
          "exception": "java.lang.OutOfMemoryError",
          "message": "The JVM ran out of memory before the validator could write its reports."
        }
      ]
    }
  ]
}
EOF
