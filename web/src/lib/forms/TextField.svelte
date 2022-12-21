<script>
  import FieldInput from './FieldInput.svelte';
  import LabeledField from './LabeledField.svelte';

  /** @type {string} */
  export let hint = '';

  /** @type {string } */
  export let id;

  /** @type {string} */
  export let label;
</script>

<LabeledField {id} {label} {hint}>
  <FieldInput {id} {...$$props} />
</LabeledField>
