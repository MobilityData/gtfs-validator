<script>
  /** @type {function(MouseEvent):void} */
  export let handleDismiss;
</script>

<div class="text-center">
  <div class="alert inline-block text-left">
    <div class="flex items-baseline">
      <i class="fa-solid fa-triangle-exclamation mr-2 text-amber-500" />
      <slot />
      <button
        type="button"
        class="close-button px-4 -mr-4"
        on:click={handleDismiss}
      >
        <i class="fa-solid fa-times" /></button
      >
    </div>
  </div>
</div>

<style lang="postcss">
  .alert {
    @apply bg-yellow-50 text-yellow-800;
    @apply border border-amber-200 rounded;
    @apply px-4 py-2;
    @apply shadow-lg shadow-yellow-400/10;
  }

  .close-button {
    @apply text-amber-800/50;
    @apply enabled:hover:text-amber-800;
    @apply enabled:active:text-amber-800/30;
  }
</style>
