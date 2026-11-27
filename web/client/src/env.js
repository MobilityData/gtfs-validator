import { defineEnvVars } from '@sveltejs/kit/env';

// Public variables read from the build environment (web/pipeline/*.env, .env.development).
export const variables = defineEnvVars({
  PUBLIC_CLIENT_API_ROOT: { public: true, description: 'Base URL of the validator web service API' },
  PUBLIC_CLIENT_REPORTS_ROOT: { public: true, description: 'Base URL where validation reports are served' },
});
