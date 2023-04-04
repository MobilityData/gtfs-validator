<script>
  import Button from '$lib/Button.svelte';
  import '../app.css';

  // Fixes issue where clicking rule hash link scrolls too far
  const handleHashchange = function() {
    window.scrollTo(window.scrollX, window.scrollY - 60);
  }
</script>

<header class="my-4 md:mb-8 lg:mb-16">
  <div class="container">
    <a href="https://mobilitydata.org/">
      <img class="logo" src="mobilitydata-logo-purple.svg" alt="MobilityData" />
    </a>
  </div>
</header>

<div class="flex-1">
  <slot />
</div>

<footer>
  <div class="container my-4">
    <div class="container my-4 flex flex-col items-center content-center space-y-4">
      <Button href="https://share.mobilitydata.org/validator-feedback" target="_blank">Help Us by Sharing Feedback</Button>
      <p class="text-mobi-purple-safe">
        <span class="inline-block">
          Made with
          <i class="fa-regular fa-heart" />
          <span class="fa-sr-only">"love"</span>
          by <a href="https://dot.ca.gov/cal-itp">Cal-ITP</a>
          and&nbsp;<a href="https://jarv.us">Jarvus</a>.
        </span>
        <span class="inline-block">
          Maintained with
          <i class="fa-regular fa-heart" />
          <span class="fa-sr-only">"love"</span>
          by&nbsp;<a href="https://mobilitydata.org/">MobilityData</a>.
        </span>
        <span class="inline-block">
          Contribute on
          <a href="https://github.com/MobilityData/gtfs-validator">Github</a>.
        </span>
      </p>
    </div>

    <p
      class="text-gray-300 mt-16 mb-8 text-sm hover:text-gray-500 transition-colors"
    >
      For performance optimization reasons, any uploaded GTFS feeds can be
      stored on MobilityData’s servers for 30 days. These feeds are not made
      publicly available nor shared with any third party, unless required to
      solve hosting issues. Our servers are located in the United States. If you
      have any concerns with this, please contact us at
      <a href="mailto:hello@mobilitydata.org">hello@mobilitydata.org</a>.
    </p>
  </div>
</footer>

<svelte:window on:hashchange|preventDefault={handleHashchange} />
