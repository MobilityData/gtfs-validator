<script>
  /** @type {string} */
  export let baseClass = 'input-control';

  /** @type {string } */
  export let className = '';

  /** @type {string} */
  export let id;

  /** @type {string} */
  export let name = id;

  /** @type {string} */
  export let type = 'text';

  $: inputClass = [baseClass, 'block w-full', className].join(' ');
</script>

<input {id} {name} {type} {...$$props} class={inputClass} />
