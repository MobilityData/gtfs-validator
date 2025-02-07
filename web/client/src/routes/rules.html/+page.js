/** @type {import('./$types').PageLoad} */

export const load = async ({ fetch }) => {
  let msgHeading, msgBody, rules, summaryMetadata = null;

  // Retrieve rules documentation from rules.json
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

  // Retrieve summary documentation from summary-metadata.json
  try {
    const response = await fetch('/summary-metadata.json');

    if (response.ok) {
      summaryMetadata = await response.json();
    } else {
      summaryMetadata = [];
      throw new Error(`HTTP Error: ${response.status}`);
    }
  }
  catch (error) {
    let errorMsg = '';

    if (error instanceof Error && error.message) {
      errorMsg = error.message;
    }

    msgHeading = errorMsg ?? 'Error';
    msgBody = 'There was a problem loading the summary metadata file.';
  }

  return { rules, summaryMetadata, msgHeading, msgBody };
};

export const prerender = true;
export const ssr = true;
export const csr = true;
