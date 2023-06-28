/** @type {import('./$types').PageLoad} */

export const load = async ({ fetch }) => {
  let msgHeading, msgBody, rules = null;

  try {
    const response = await fetch('/rules.json');

    if (response.ok) {
      rules = await response.json();
    } else {
      throw new Error(`HTTP Error: ${response.status}`);
    }
  }
  catch (error) {
    let errorMsg = '';

    if (error instanceof Error && error.message) {
      errorMsg = error.message;
    }

    msgHeading = errorMsg ?? 'Error';
    msgBody = 'There was a problem loading the rules file.';
  }

  return { rules, msgHeading, msgBody };
};

export const prerender = true;
export const ssr = true;
export const csr = true;
