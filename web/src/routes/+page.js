/** @type {import('./$types').PageLoad} */
export async function load({ fetch }) {
  // get unique upload URL from the server and pass it into the page
  const response = await fetch(
    'https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app/upload-url'
  );
  const data = await response.json();

  return { upload: data };
}
