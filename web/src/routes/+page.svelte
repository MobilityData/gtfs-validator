<script>
  import Alert from '$lib/Alert.svelte';
  import Button from '$lib/Button.svelte';
  import DropTarget from '$lib/DropTarget.svelte';
  import FileField from '$lib/forms/FileField.svelte';
  import Form from '$lib/forms/Form.svelte';
  import TextField from '$lib/forms/TextField.svelte';

  import { fly } from 'svelte/transition';
  import { quintOut } from 'svelte/easing';

  import axios from 'axios';

  /** @type {import('./$types').PageData} */
  export let data;

  let allowUrl = false;
  let showDocs = true;

  /** @type {string[]} */
  let errors = [];

  /** @type {string} */
  let pendingFilename = '';

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
      if (files.length > 1) {
        errors.push('Sorry, you must upload only one ZIP file.');
      } else {
        handleFile(files[0]);
      }
    }
  }

  /** @param {Event} event */
  function handleFileInput(event) {
    // @ts-ignore - TODO fix the types here?
    const files = event?.target?.files;
    handleFiles(files);
  }

  /** @param {File} file */
  function handleFile(file) {
    console.log('handling file', file.name);
    clearFile();

    if (file.type != 'application/zip') {
      console.log('file type error', file.type);
      errors.push('Sorry, only ZIP files are supported at this time.');
      console.log(errors);
    } else {
      pendingFilename = file?.name;
      uploadFile(file);
    }
  }

  /** @param {FileList} files */
  function handleFiles(files) {
    console.log('handling files', files);
    clearFile();
    if (files.length > 1) {
      errors.push('Sorry, you must upload only a single ZIP file.');
    } else if (files.length == 1) {
      handleFile(files[0]);
    }
  }

  /** @param {SubmitEvent} event */
  function handleSubmit(event) {
    event.preventDefault();

    // @ts-ignore - TODO fix the types here?
    handleFiles(event.target.file.files);
  }

  /** @param {File} file */
  async function uploadFile(file) {
    // TODO fix this up (is FileReader necessary)
    // TODO resolve CORS issues
    // TODO improve UX
    const reader = new FileReader();
    console.log('uploading', file);
    reader.readAsBinaryString(file);
    reader.addEventListener('load', () => {
      const raw = reader.result;
      console.log('axios', axios);
      axios.put(data.upload.url, {
        headers: { 'Content-Type': 'application/octet-stream' },
        data: raw,
      });
      console.log('data', raw);
    });
  }
</script>

<div class="container">
  <h1 class="h1 leading-tight mb-0">Canonical GTFS Schedule Validator</h1>
</div>

<div class="bg-mobi-light-gray">
  <DropTarget {handleDragOver} {handleDrop}>
    <div class="container">
      <Form {handleSubmit} action={data.upload.url} method="PUT">
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
          />

          {#if allowUrl}
            <TextField
              label="Or load from a URL"
              id="url"
              placeholder="https://example.com/feed.zip"
              type="url"
            />
          {/if}
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

          <Button disabled={!pendingFilename} type="submit" variant="primary"
            >Validate</Button
          >
        </div>
      </Form>
    </div>
  </DropTarget>
</div>
