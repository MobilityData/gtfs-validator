<script>
  import { browser } from '$app/environment';

  import Alert from '$lib/Alert.svelte';
  import Button from '$lib/Button.svelte';
  import DropTarget from '$lib/DropTarget.svelte';
  import FileField from '$lib/forms/FileField.svelte';
  import Form from '$lib/forms/Form.svelte';
  import TextField from '$lib/forms/TextField.svelte';
  import SelectField from '$lib/forms/SelectField.svelte';
  import StatusModal from '$lib/StatusModal.svelte';

  import countryCodes from '$lib/countryCodes';

  import { fly } from 'svelte/transition';
  import { onMount, tick } from 'svelte';
  import { quintOut } from 'svelte/easing';

  /**
   * @typedef CreateJobParameters
   * @type {object}
   * @property {string=} url - job source url.
   * @property {string} countryCode - country code.
   */

  /**
   * @typedef { 'error'
   *  | 'authorizing'
   *  | 'uploading'
   *  | 'processing'
   *  | 'ready'
   * }
   * Status
   */

  let showDocs = true;

  const apiRoot = 'https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app';

  /** @type {HTMLInputElement} */
  let fileInput;

  /** @type {HTMLFormElement|undefined} */
  let form;

  /** @type {string} */
  let jobId;

  /** @type {boolean} */
  let jobInProgress;

  /** @type {string[]} */
  let errors = [];

  /** @type {string} */
  let pendingFilename = '';

  /** @type {string} */
  let region;

  /** @type {string} */
  let sourceUrl = '';

  /** @type {Status} */
  let status;

  /** @type {HTMLDialogElement} */
  let statusModal;

  $: reportUrl = `https://gtfs-validator-results.mobilitydata.org/${jobId}/report.html`;

  function clearErrors() {
    errors = [];
  }

  function clearFile() {
    clearErrors();
    pendingFilename = '';
  }

  /** @param {DragEvent} event */
  function handleDragOver(event) {
    event.preventDefault();
  }

  function detectRegion() {
    if (browser) {
      // attempt to detect region automatically
      const browserLanguage = window?.navigator?.language;
      if (browserLanguage && browserLanguage.split('-').length > 1) {
        const code = navigator.language.split('-')[1];
        const regionIndex = countryCodes.findIndex(
          (country) => country.value === code
        );
        // copy detected region to the top of the list, if it's not already first
        if (regionIndex > 0) {
          const region = countryCodes[regionIndex];
          countryCodes.unshift({ value: '', label: '---------------------' });
          countryCodes.unshift(region);
        }
      }
    }
  }

  /** @param {DragEvent} event */
  function handleDrop(event) {
    event.preventDefault();
    clearFile();

    const files = event.dataTransfer?.files;
    if (files) {
      if (files.length < 1) {
        addError('Sorry, you can only drop a ZIP file here.');
      } else if (files.length > 1) {
        addError('Sorry, you must upload only one ZIP file.');
      } else {
        handleFile(files[0], true);
      }
    }
  }

  /** @param {Event} event */
  function handleFileInput(event) {
    // @ts-ignore - TODO fix the types here?
    const files = event?.target?.files;
    handleFiles(files);
  }

  /**
   * @param {File} file
   * @param {boolean} shouldUpload
   **/
  function handleFile(file, shouldUpload = false) {
    clearFile();

    // Zip files on MS Windows seem to have the application/x-zip-compressed mime type
    if (file.type != 'application/zip' && file.type != 'application/x-zip-compressed') {
      console.log('file type error', file.type);
      addError('Sorry, only ZIP files are supported at this time.');
      console.log(errors);
    } else {
      pendingFilename = file?.name;
      if (shouldUpload) {
        generateReport(file);
      }
    }
  }

  /** @param {FileList} files */
  function handleFiles(files, shouldUpload = false) {
    clearFile();
    if (files.length > 1) {
      addError('Sorry, you must upload only a single ZIP file.');
    } else if (files.length == 1) {
      handleFile(files[0], shouldUpload);
    }
  }

  async function handleUrlParams() {
    // if there's a report id in the url like ?report=[id]
    // let's try to show the report info in a StatusModal
    const params = new URLSearchParams(window.location.search);
    const reportId = params.get('report');
    if (reportId) {
      jobId = reportId;
      await tick();
      const exists = await reportExists().catch(() => false);
      updateStatus(exists ? 'ready' : 'error');
    } else {
      // if there's no id, clear status modal
      statusModal.close();
    }
  }

  /** @param {Event} event */
  function handleReset(event) {
    event.preventDefault();
    // store selected region since it's unlikely to change
    const selectedRegion = region;
    if (form) form.reset();

    // restore values after native reset
    setTimeout(() => {
      region = selectedRegion;
      sourceUrl = '';
      clearFile();
    }, 0);
  }

  /** @param {SubmitEvent} event */
  function handleSubmit(event) {
    event.preventDefault();

    const file = fileInput?.files?.item(0);

    if (file) {
      generateReport(file);
    } else if (sourceUrl) {
      generateReport(sourceUrl);
    } else {
      addError('Please include a file to validate.');
    }
  }

  /** @param {string=} url **/
  function createJob(url) {
    updateStatus('authorizing');
    jobInProgress = false; // stop any ongoing polling loops
    jobId = '_waiting_';

    return new Promise((resolve, reject) => {
      /** @type {CreateJobParameters} */
      const data = {
        countryCode: region,
      };

      if (url) {
        data.url = url;
      }

      const xhr = new XMLHttpRequest();
      xhr.responseType = 'json';
      xhr.onerror = () => reject('Error authorizing upload.');
      xhr.onload = () => resolve(xhr.response);
      xhr.open('POST', `${apiRoot}/create-job`);
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.send(JSON.stringify(data));
    });
  }

  /** @param {Status} newStatus */
  function updateStatus(newStatus) {
    status = newStatus;

    if (status != null) {
      if (!statusModal.open) {
        statusModal.showModal();
      }
    }
  }

  /**
   * @param {string} url
   * @param {File} file
   **/
  function putFile(url, file) {
    updateStatus('uploading');
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.onload = resolve;
      xhr.onerror = () => reject('Error uploading file.');
      xhr.open('PUT', url);
      xhr.setRequestHeader('Content-Type', 'application/octet-stream');
      xhr.send(file);
    });
  }

  async function waitForJob() {
    return new Promise(async (resolve, reject) => {
      updateStatus('processing');
      jobInProgress = true;

      do {
        try {
          jobInProgress = !(await reportExists());
          if (jobInProgress) {
            await sleep(2500);
          }
        } catch (error) {
          jobInProgress = false;
          reject('Error processing report.');
        }
      } while (jobInProgress);

      jobInProgress = false;
      resolve(!jobInProgress);
    });
  }

  function reportExists() {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();

      xhr.onload = () => resolve(xhr.status === 200);
      xhr.onerror = reject;
      xhr.open('HEAD', reportUrl);
      xhr.send();
    });
  }

  /** @param {string} errorText */
  function addError(errorText) {
    // use assignment because .push() doesn't always trigger reactivity
    errors = [...errors, errorText];
  }

  /** @param {number} ms */
  function sleep(ms) {
    return new Promise((res) => setTimeout(res, ms));
  }

  /** @param {File|string} source */
  async function generateReport(source) {
    let canContinue = true;

    // if source is a URL, pass it to createJob
    const url = typeof source === 'string' ? source : undefined;

    // get a job id from the server
    const job = await createJob(url).catch((error) => {
      addError(error);
      canContinue = false;
    });

    if (canContinue) {
      jobId = job.jobId;

      // push a url with the ID so it can be referred to later (see handleUrlParams)
      history.pushState(null, '', `?report=${jobId}`);

      // upload the file (if applicable)
      if (source instanceof File) {
        await putFile(job.url, source).catch((error) => {
          addError(error);
          canContinue = false;
        });
      }
    }

    // poll for report ready
    if (canContinue) {
      await waitForJob().catch((error) => {
        addError(error);
        canContinue = false;
      });
    }

    // clean up
    if (canContinue) {
      updateStatus('ready');

      if (form) {
        form.reset();
        clearFile();
      }
    }

    // hide progress modal if there's an error
    if (!canContinue) {
      statusModal.close();
    }
  }

  onMount(() => {
    form = /** @type {HTMLFormElement} */ (
      document.getElementById('validator-form')
    );

    detectRegion();

    handleUrlParams();
    addEventListener('popstate', handleUrlParams);
  });
</script>

<div class="container">
  <h1 class="h1 leading-none mb-0">Canonical GTFS Schedule Validator</h1>
</div>

<div class="bg-mobi-light-gray">
  <DropTarget {handleDragOver} {handleDrop}>
    <div class="container">
      <Form id="validator-form" on:submit={handleSubmit} on:reset={handleReset}>
        <h2 class="h3 text-center">
          Evaluate your dataset against the official
          <a
            href="https://gtfs.org/schedule/reference/"
            target="_blank"
            rel="noreferrer">GTFS Reference</a
          >
          and
          <a
            href="https://gtfs.org/schedule/best-practices/"
            target="_blank"
            rel="noreferrer">Best&nbsp;Practices</a
          >.
        </h2>

        <div class="max-w-xl mx-auto">
          <FileField
            label="Upload a ZIP file"
            id="file"
            hint="You can also drag a file here"
            accept="application/zip,.zip"
            filename={pendingFilename}
            on:input={handleFileInput}
            bind:fileInput
          />

          <TextField
            label="Or load from a URL"
            id="url"
            placeholder="https://example.com/feed.zip"
            type="url"
            bind:value={sourceUrl}
          />

          <SelectField
            id="region"
            name="region"
            label="Region (optional)"
            hint={region}
            bind:value={region}
            options={countryCodes}
            placeholder="Choose a region"
          />
        </div>

        {#if errors.length > 0}
          <div transition:fly|local={{ y: -10, easing: quintOut }}>
            <Alert handleDismiss={clearErrors}>
              <ul>
                {#each errors as error}
                  <li>{error}</li>
                {/each}
              </ul>
            </Alert>
          </div>
        {/if}

        <div
          class="flex flex-col md:flex-row md:justify-end md:w-auto mt-16 gap-4"
        >
          {#if showDocs}
            <Button href="/rules.html">See Documentation</Button>
          {/if}

          <Button type="submit" variant="primary">Validate</Button>
        </div>
      </Form>
    </div>
  </DropTarget>
</div>

<StatusModal
  bind:dialog={statusModal}
  bind:status
  {reportUrl}
  on:close={() => {
    status = 'processing';
  }}
/>
