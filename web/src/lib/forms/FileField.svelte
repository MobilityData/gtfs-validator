<script>
  import Button from '$lib/Button.svelte';
  import LabeledField from './LabeledField.svelte';

  /** @type {HTMLInputElement} */
  let fileInput;

  /** @type {string} */
  export let filename;

  /** @type {(function(Event):void)|null} */
  export let handleInput = null;

  /** @type {string} */
  export let hint = '';

  /** @type {string } */
  export let id;

  /** @type {string} */
  export let label;

  /** @type {string} */
  export let name = id;

  function clickFileInput() {
    fileInput.click();
  }

  $: {
    if (filename == '' && fileInput?.value) {
      fileInput.value = fileInput.defaultValue;
    }
  }
</script>

<LabeledField {id} {label} {hint}>
  <input
    readonly
    class="input-control block w-full mb-2"
    on:click={clickFileInput}
    placeholder="No file chosen"
    value={filename}
  />

  <Button type="button" className="py-1" handleClick={clickFileInput}>
    Choose a file&hellip;
  </Button>

  <input
    {id}
    {name}
    class="sr-only"
    type="file"
    {...$$restProps}
    bind:this={fileInput}
    on:input={handleInput}
  />
</LabeledField>
