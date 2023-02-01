<script>
  import { browser } from '$app/environment';

  import Alert from '$lib/Alert.svelte';
  import Button from '$lib/Button.svelte';
  import DropTarget from '$lib/DropTarget.svelte';
  import FileField from '$lib/forms/FileField.svelte';
  import Form from '$lib/forms/Form.svelte';
  import languageTags from '$lib/rfc5646-language-tags';
  import TextField from '$lib/forms/TextField.svelte';
  import SelectField from '$lib/forms/SelectField.svelte';
  import StatusModal from '$lib/StatusModal.svelte';

  import { fly } from 'svelte/transition';
  import { onMount } from 'svelte';
  import { quintOut } from 'svelte/easing';

  let allowUrl = false;
  let showDocs = true;

  /** @type {HTMLInputElement} */
  let fileInput;

  /** @type {string} */
  let languageCode;

  /** @type {string} */
  let jobId;

  /** @type {string[]} */
  let errors = [];

  /** @type {string} */
  let pendingFilename = '';

  /** @type {string} */
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

  /** @param {DragEvent} event */
  function handleDrop(event) {
    event.preventDefault();
    clearFile();

    const files = event.dataTransfer?.files;
    if (files) {
      if (files.length < 1) {
        errors.push('Sorry, you can only drop a ZIP file here.');
      } else if (files.length > 1) {
        errors.push('Sorry, you must upload only one ZIP file.');
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
    console.log('handling file', file.name);
    clearFile();

    if (file.type != 'application/zip') {
      console.log('file type error', file.type);
      errors.push('Sorry, only ZIP files are supported at this time.');
      console.log(errors);
    } else {
      pendingFilename = file?.name;
      if (shouldUpload) {
        uploadFile(file);
      }
    }
  }

  /** @param {FileList} files */
  function handleFiles(files, shouldUpload = false) {
    console.log('handling files', files);
    clearFile();
    if (files.length > 1) {
      errors.push('Sorry, you must upload only a single ZIP file.');
    } else if (files.length == 1) {
      handleFile(files[0], shouldUpload);
    }
  }

  /** @param {SubmitEvent} event */
  function handleSubmit(event) {
    event.preventDefault();
    const file = fileInput?.files?.item(0);

    if (file) {
      uploadFile(file);
    }
  }

  function getUrl() {
    return new Promise((resolve, reject) => {
      const data = null;

      const xhr = new XMLHttpRequest();
      xhr.responseType = 'json';
      xhr.onerror = reject;
      xhr.addEventListener('readystatechange', function () {
        if (this.readyState === this.DONE) {
          resolve(this.response);
        }
      });

      xhr.open(
        'GET',
        'https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app/upload-url'
      );
      xhr.send(data);
    });
  }

  /** @param {string} newStatus */
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
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.onload = resolve;
      xhr.onerror = reject;
      xhr.open('PUT', url);
      xhr.setRequestHeader('Content-Type', 'application/octet-stream');
      xhr.send(file);
    });
  }

  function reportExists() {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();

      xhr.addEventListener('readystatechange', function () {
        if (this.readyState === this.DONE) {
          resolve(xhr.status === 200);
        }
      });
      xhr.onerror = reject;
      xhr.open('HEAD', reportUrl);
      xhr.send();
    });
  }

  /** @param {number} ms */
  function sleep(ms) {
    return new Promise((res) => setTimeout(res, ms));
  }

  /** @param {File} file */
  async function uploadFile(file) {
    updateStatus('authorizing');
    const result = await getUrl();
    updateStatus('uploading');
    jobId = result.jobId;
    await putFile(result.url, file);
    updateStatus('processing');

    let jobComplete = false;
    do {
      jobComplete = await reportExists();
      if (!jobComplete) {
        await sleep(2500);
      }
    } while (!jobComplete);

    updateStatus('ready');

    const form = /** @type {HTMLFormElement} */ (
      document.getElementById('validator-form')
    );

    if (form) {
      form.reset();
      clearFile();
    }
  }

  onMount(() => {
    if (browser) {
      // attempt to detect language automatically
      const browserLanguage = window?.navigator?.language;
      if (browserLanguage && browserLanguage in languageTags) {
        languageCode = navigator.language;
      }
    }
  });
</script>

<div class="container">
  <h1 class="h1 leading-none mb-0">Canonical GTFS Schedule Validator</h1>
</div>

<div class="bg-mobi-light-gray">
  <DropTarget {handleDragOver} {handleDrop}>
    <div class="container">
      <Form id="validator-form" {handleSubmit}>
        <h2 class="h3 text-center">
          Check the quality of a file {#if allowUrl}or a feed{/if}
        </h2>

        <div class="max-w-xl mx-auto">
          <FileField
            label="Upload a ZIP file"
            id="file"
            hint="You can also drag a file here"
            accept="application/zip,.zip"
            filename={pendingFilename}
            handleInput={handleFileInput}
            bind:fileInput
          />

          {#if allowUrl}
            <TextField
              label="Or load from a URL"
              id="url"
              placeholder="https://example.com/feed.zip"
              type="url"
            />
          {/if}

          <SelectField
            id="languageCode"
            name="languageCode"
            label="Language"
            bind:value={languageCode}
            options={languageTags}
            placeholder="Choose a language"
          />
        </div>

        {#if errors.length > 0}
          <div transition:fly={{ y: -10, easing: quintOut }}>
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
            <Button
              href="https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md"
              target="_blank"
            >
              See Documentation
              <i class="fa-solid fa-xs fa-arrow-up-right-from-square" />
            </Button>
          {/if}

          <Button disabled={!pendingFilename} type="submit" variant="primary">
            Validate
          </Button>
        </div>
      </Form>
    </div>
  </DropTarget>
</div>

<StatusModal bind:dialog={statusModal} bind:status {reportUrl} />
