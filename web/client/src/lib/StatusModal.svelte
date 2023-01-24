<script>
  import { backOut, circInOut, elasticOut } from 'svelte/easing';
  import { fade, fly, scale, slide } from 'svelte/transition';

  import Button from "./Button.svelte";

  /** @type {?HTMLDialogElement} */
  export let dialog = null;

  /** @type {string} */
  export let status;

  /** @type {string} */
  export let reportUrl;

  $: isReady = status === 'ready';

  function handleDismiss() {
    console.log('handle dismiss');
    if (dialog) {
      dialog.close();
    }
  }

  /** @type {Object.<string, string>} */
  const statusMessages = {
    authorizing: 'Preparing upload\u2026',
    uploading: 'Uploading file\u2026',
    processing: 'Validating GTFS data\u2026',
    ready: 'Your report is\u00a0ready!',
  };

  /** @type {string[]} */
  // TODO some kind of shared enum?
  const statusSteps = [
    'authorizing',
    'uploading',
    'processing',
    'ready',
  ];
</script>

<dialog bind:this={dialog} class="backdrop:bg-mobi-light-blue/20 backdrop:backdrop-blur-sm
  rounded-lg shadow-2xl shadow-indigo-800/50
  overflow-hidden
  p-8 w-80
  text-center
  transition-all
">
  {#if status == 'ready'}
    <form method="dialog" class="flex justify-end -mt-7 -mr-7">
      <span in:scale>
        <Button type="submit" className="px-4" variant="link">
          <i class="fa-solid fa-times"></i>
          <div class="sr-only">Dismiss</div>
        </Button>
      </span>
    </form>
  {/if}

  <div class="grid">
    {#key status}
      <div style="grid-area: 1/1/1/1" in:fly={{ easing: circInOut, x: 128 }} out:fly={{ easing: circInOut, x: -128 }}>
        {#if status == 'authorizing'}
          <i class="fa-solid fa-2x fa-file-lines fa-flip text-mobi-purple"></i>
        {:else if status == 'uploading'}
          <i class="fa-solid fa-2x fa-cloud-arrow-up fa-beat text-mobi-purple"></i>
        {:else if status == 'processing'}
          <i class="fa-solid fa-2x fa-gear fa-spin text-mobi-purple-safe" style="--fa-animation-duration: 1s"></i>
        {:else if status == 'ready'}
          {#key status}
            <i in:scale={{ delay: 400, duration: 800, easing: elasticOut }}
              class="fa-solid fa-2x fa-check-circle text-mobi-light-blue"></i>
          {/key}
        {/if}
      </div>
    {/key}
  </div>

  {#key status}
    <div class="status-message leading-snug mt-4" in:fade={{ duration: 250 }}>
      {statusMessages[status]}
    </div>
  {/key}

  {#if status == 'authorizing' || status == 'processing'}
    <div in:slide={{ delay: 5000 }} out:slide class="opacity-40">This may take a&nbsp;moment</div>
    <div in:slide={{ delay: 20000 }} out:slide class="opacity-40">Hang tight!</div>
  {/if}

  {#if isReady && reportUrl}
    <div transition:slide={{ easing: backOut }} class="mt-8 flex flex-col">
      <Button href={reportUrl} target="_blank">
        Open Report
        <i class="fa-solid fa-xs fa-arrow-up-right-from-square" />
      </Button>
    </div>
  {/if}
</dialog>
