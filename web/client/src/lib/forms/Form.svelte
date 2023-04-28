<script>
  const defaultClass = 'bg-mobi-light-gray py-8';

  /** @type {string} */
  export let className = '';
</script>

<form
  class={`${className} ${defaultClass}`}
  on:submit
  on:reset
  {...$$restProps}
>
  <slot />
</form>
