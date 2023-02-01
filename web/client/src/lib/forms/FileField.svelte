<script>
  import Button from '$lib/Button.svelte';
  import LabeledField from './LabeledField.svelte';

  /** @type {string} */
  export let buttonText = 'Choose a file\u2026';

  /** @type {HTMLInputElement} */
  export let fileInput;

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

  /** @type {string} */
  export let placeholder = 'No file chosen';

  function clickFileInput() {
    fileInput.focus();
    fileInput.click();
  }

  $: {
    if (filename == '' && fileInput?.value) {
      fileInput.value = fileInput.defaultValue;
    }
  }
</script>

<LabeledField {id} {label} {hint}>
  <div
    class="input-control focus-ring mb-2 py-3"
    on:click={clickFileInput}
    on:keypress={clickFileInput}
  >
    <div class={filename ? null : 'text-black/50'}>
      {filename ? filename : placeholder}
    </div>

    <input
      {id}
      {name}
      class="sr-only"
      type="file"
      {...$$restProps}
      bind:this={fileInput}
      on:input={handleInput}
    />
  </div>

  <Button type="button" className="py-1" handleClick={clickFileInput}>
    {buttonText}
  </Button>
</LabeledField>
