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

  $: inputClass = [baseClass, 'block focus-ring w-full', className].join(' ');
</script>

<slot>
  <input {id} {name} {type} {...$$restProps} class={inputClass} />
</slot>
