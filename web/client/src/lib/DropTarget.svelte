<script>
  import { createEventDispatcher } from 'svelte';

  /** @type {function(DragEvent):void} */
  export let handleDragOver;

  /** @type {function(DragEvent):void} */
  export let handleDrop;

  let dragEnterCounter = 0;

  // dragEnter/leave will fire on children
  // we're only interested in the parent
  // so track the "level" instead of events
  $: isDragging = dragEnterCounter > 0;

  /** @param {DragEvent} event */
  function handleDragEnter(event) {
    dragEnterCounter++;
  }

  /** @param {DragEvent} event */
  function handleDragOverInternal(event) {
    event.preventDefault();
    handleDragOver(event);
  }

  function handleDragLeave() {
    dragEnterCounter--;
  }

  /** @param {DragEvent} event */
  function handleDropInternal(event) {
    event.preventDefault();
    dragEnterCounter = 0;
    handleDrop(event);
  }
</script>

<div
  class="drop-target-container relative"
  on:dragenter={handleDragEnter}
  on:dragover={handleDragOverInternal}
  on:dragleave={handleDragLeave}
  on:drop={handleDropInternal}
>
  <div class="drop-target-overlay" hidden={!isDragging}>
    <slot name="overlay">
      <i class="fa-regular fa-file fa-4x fa-beat mb-4" />
      <p class="text-xl text-center">
        Drop your file here.<br />
        It will upload immediately.
      </p>
    </slot>
  </div>
  <div class="drop-target-contents">
    <slot />
  </div>
</div>

<style lang="postcss">
  .drop-target-overlay {
    @apply bg-mobi-light-blue/75 backdrop-blur-sm text-white;
    @apply absolute w-full h-full z-10;
    @apply flex flex-col items-center justify-center;
  }
</style>
