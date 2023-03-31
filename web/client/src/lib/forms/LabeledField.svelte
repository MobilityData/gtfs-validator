<script>
  /** @type {string} */
  export let hint = '';

  /** @type {string} */
  export let id;

  /** @type {string} */
  export let label;
</script>

<div class="field my-8">
  <slot name="label">
    <label class="inline-block mb-1" for={id}>{label}</label>
  </slot>
  <slot />
  <slot name="hint">
    {#if hint}
      <div class="field-hint mt-1 text-sm text-black/50">{hint}</div>
    {/if}
  </slot>
</div>
