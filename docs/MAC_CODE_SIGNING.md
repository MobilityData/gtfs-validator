I created a Keychain file just for development named mobilitydata.

```
$ security find-identity
Policy: X.509 Basic
  Matching identities
  1) 02E2E5C2E035C4AEB7DEA380CEC56C1F2A5DADA4 "Developer ID Application: The International Data Organization For Transport (BF2U75HN4D)"
     X identities found

  Valid identities only
  1) 02E2E5C2E035C4AEB7DEA380CEC56C1F2A5DADA4 "Developer ID Application: The International Data Organization For Transport (BF2U75HN4D)"
     X valid identities found
```

If identity is marked with `CSSMERR_TP_NOT_TRUSTED`, that probably means you need to install the
`Developer ID - G2` Apple Intermediate Certificate from https://www.apple.com/certificateauthority/

```
security import "Developer ID Application Certificate.pem" -k ~/Library/Keychains/mobilitydata.keychain-db
security import "Developer ID Application Certificate.p12" -k ~/Library/Keychains/mobilitydata.keychain-db
```

```
$ codesign --verify --deep -vvvv app/pkg/build/jpackage/GTFS\ Validator.app/
app/pkg/build/jpackage/GTFS Validator.app/: valid on disk
app/pkg/build/jpackage/GTFS Validator.app/: satisfies its Designated Requirement
```
