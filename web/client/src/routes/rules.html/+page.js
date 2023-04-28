/** @type {import('./$types').PageLoad} */

export const load = async ({ fetch }) => {
  let rulesMd = '';

  try {
    // local copy of https://raw.githubusercontent.com/MobilityData/gtfs-validator/master/RULES.md
    // we could fetch it directly instead, if desired
    const response = await fetch('/RULES.md');

    if (response.ok) {
      rulesMd = await response.text();
    } else {
      throw new Error(`HTTP Error: ${response.status}`);
    }
  }
  catch (error) {
    let msg = 'Error';

    if (error instanceof Error && error.message) {
      msg = error.message;
    }

    rulesMd = `# ${msg}\n\nThere was a problem loading the rules file. You can try accessing it directly at https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md.`;
  }

  return { rulesMd };
};

export const prerender = true;
export const ssr = true;
export const csr = true;
