<script>
  import FieldInput from './FieldInput.svelte';
  import LabeledField from './LabeledField.svelte';

  /** @type {string} */
  export let hint = '';

  /** @type {string } */
  export let id;

  /** @type {string} */
  export let label;

  /** @type {string} */
  export let baseClass = 'input-control';

  /** @type {string } */
  let className = '';
  export { className as class };

  /** @type {string} */
  export let name = id;

  /** @type {string} */
  export let placeholder = 'Choose an option';

  /** @type {string} */
  export let value;

  /** @type {Array<string>|Object|?} */
  export let options = {
    foo: 'bar',
    baz: 'qux',
  };

  $: inputClass = ['focus-ring', className].join(' ');

  $: optionsArray = Array.isArray(options)
    ? options.map((item) => [item, item])
    : Object.entries(options);
</script>

<LabeledField {id} {label} {hint}>
  <FieldInput {id}>
    <div class="select-wrapper {baseClass}">
      <select {id} {name} bind:value class={inputClass} {...$$restProps}>
        <slot name="options" {options}>
          <option value="" selected disabled>{placeholder}</option>
          {#each optionsArray as [value, label]}
            <option {value}>{label}</option>
          {/each}
        </slot>
      </select>

      <div class="trigger-icon">
        <i aria-hidden="true" class="fa-fw fa-solid fa-angle-down" />
      </div>
    </div>
  </FieldInput>
</LabeledField>

<style lang="postcss">
  .select-wrapper {
    @apply relative;
  }

  .select-wrapper .trigger-icon {
    @apply absolute right-0 top-0
      flex flex-col items-center justify-center
      h-12 w-10;
  }

  .input-control {
    @apply p-0 rounded-lg shadow;
  }

  .input-control select {
    @apply appearance-none w-full h-full rounded-lg
      pl-3 pr-9 py-2
      truncate;
  }
</style>
