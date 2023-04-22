<script>
  import Button from './Button.svelte';
  import { fly } from 'svelte/transition';

  /** @type {string} */
  let className = '';
  export { className as class };

  /** @type {string} */
  export let content = '';

  /** @type {string?} */
  export let href = null;

  /** @type {string?} */
  export let ready = null;

  /** @type {'sm'|'md'} */
  export let size = 'md';

  /** @type {'ready'|'waiting'|'error'|'done'} */
  let status = 'ready';

  /** @param {MouseEvent} event */
  async function handleClick(event) {
    event.preventDefault();

    status = 'waiting';
    try {
      // attempt to write to clipboard
      await navigator.clipboard.writeText(content);
      status = 'done';
    } catch (e) {
      console.error(e);
      status = 'error';
    } finally {
      resetIcon();
    }
  }

  /** @param {number} delay ms */
  function resetIcon(delay = 3500) {
    setTimeout(() => (status = 'ready'), delay);
  }
</script>

<Button bind:class={className} {href} {size} on:click={handleClick}>
  <div class="inline-grid overflow-hidden">
    {#key status}
      <div
        in:fly|local={{ y: 20 }}
        out:fly|local={{ y: -20 }}
        style="grid-area: 1 / 1 / 1 / 1"
      >
        {#if ['ready', 'waiting'].includes(status)}
          {#if ready}
            {ready}
          {:else}
            <slot />
          {/if}

          {#if status == 'waiting'}
            <i
              class="fa-solid fa-circle-notch fa-spin"
              style="--fa-animation-duration: 500ms"
            />
          {:else}
            <i class="fa-regular fa-sm fa-clone" data-content={content} />
          {/if}
        {:else if status == 'done'}
          Copied
          <i class="fa-solid fa-check" />
        {:else}
          Error
          <i class="fa-solid fa-ban" />
        {/if}
      </div>
    {/key}
  </div>
</Button>
