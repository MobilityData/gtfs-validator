<script>
  const defaultClass = 'bg-mobi-light-gray py-8';

  /** @type {string} */
  export let className = '';

  /** @type {function(SubmitEvent):void} */
  export let handleSubmit;
</script>

<form
  class={`${className} ${defaultClass}`}
  on:submit={handleSubmit}
  {...$$restProps}
>
  <slot />
</form>
