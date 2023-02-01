<script>
  import '../app.css';
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
    </p>
  </div>
</footer>
