<script>
  import ButtonContent from './ButtonContent.svelte';

  /** @type {string} */
  export let className = '';

  /** @type {boolean|null} */
  export let disabled = null;

  /** @type {(function():void)|null} */
  export let handleClick = null;

  /** @type {string|null} */
  export let href = null;

  /** @type {string|null} */
  export let target = null;

  /** @type {'button'|'submit'|'reset'|null|undefined} */
  export let type = 'button';

  /** @type {string} */
  export let variant = 'default';

  $: buttonClass = [
    'btn',
    `btn-${variant}`,
    `focus-ring`,
    disabled ? 'cursor-not-allowed opacity-60' : '',
    'inline-block',
    'font-mono',
    'px-8 py-2',
    'rounded',
    'shadow',
    'transition-transform duration-100 [&:not(:disabled)]:active:scale-95',
    className,
  ].join(' ');

  $: isLink = href != null;
</script>

{#if isLink}
  <a class={buttonClass} {href} {target} on:click={handleClick}>
    <slot name="content">
      <ButtonContent><slot /></ButtonContent>
    </slot>
  </a>
{:else}
  <button class={buttonClass} {type} {disabled} on:click={handleClick}>
    <slot name="content">
      <ButtonContent {disabled}><slot /></ButtonContent>
    </slot>
  </button>
{/if}

<style lang="postcss">
  .btn-default {
    @apply bg-white [&:not(:disabled)]:hover:bg-indigo-50;
    @apply text-mobi-purple-safe;
    @apply border border-mobi-purple-safe;
    @apply shadow-mobi-purple/30;
  }

  .btn-primary {
    @apply bg-mobi-light-blue [&:not(:disabled)]:hover:bg-mobi-light-blue/80;
    @apply text-white;
    @apply border-b border-black/30;
    @apply shadow-mobi-purple;
  }

  .btn-link {
    @apply bg-none border-0 shadow-none text-mobi-purple-safe;
    @apply px-4;
  }
</style>
