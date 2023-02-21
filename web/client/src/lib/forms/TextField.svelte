<script>
  import LabeledField from './LabeledField.svelte';

  /** @type {string} */
  export let hint = '';

  /** @type {string} */
  export let id;

  /** @type {string?} */
  export let inputClass = 'input-control block focus-ring w-full';

  /** @type {string} */
  export let label;

  /** @type {string} */
  export let type = 'text';

  /** @type {string} */
  export let value;

  /** @param {HTMLInputElement} node */
  const setType = (node) => {
    // work around svelte dynamic type + two-way binding conflict
    // https://github.com/sveltejs/svelte/issues/3921#issuecomment-880664654
    // should be ok because this component is only meant for text/strings
    node.type = type;
  };
</script>

<LabeledField {id} {label} {hint}>
  <slot>
    <input {id} use:setType bind:value {...$$restProps} class={inputClass} />
  </slot>
</LabeledField>
