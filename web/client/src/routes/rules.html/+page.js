/** @type {import('./$types').PageLoad} */

export const load = async ({ fetch }) => {
  // local copy of https://raw.githubusercontent.com/MobilityData/gtfs-validator/master/RULES.md
  // we could fetch it directly instead, if desired
  const response = await fetch('/RULES.md');
  const rulesMd = await response.text();
  return { rulesMd };
};

export const prerender = true;
export const ssr = true;
export const csr = true;
