const language = navigator.language || 'en-US'

function getCountries() {
  const A = 65
  const Z = 90

  const languageCode = language.split('-')[0]
  const countryName = new Intl.DisplayNames([languageCode], { type: 'region' });
  const countries = {} as any;

  for (let i=A; i<=Z; ++i) {
      for (let j=A; j<=Z; ++j) {
          let code = String.fromCharCode(i) + String.fromCharCode(j)
          let name = countryName.of(code)

          if (name && code !== name) {
              countries[name] = code
          }
      }
  }
  return countries
}

const countries = getCountries()

export default Object.keys(countries).sort((a,b)  => a.localeCompare(b, language)).map(name => ({
  value: countries[name],
  label: name
}))
