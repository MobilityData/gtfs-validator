/// <reference types="cypress" />

context('GTFS Validator - Core Workflow', () => {
  beforeEach(() => {
    cy.visit('https://gtfs-validator.mobilitydata.org/')
  });

  it('Validate GTFS .zip file', () => {
    const url = 'https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip';
    const jobId = '8f6be6fb-1fee-41f8-b401-b2b4b552e177';

    // Setup intercept aliases
    cy.intercept(
        'POST',
        'https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app/create-job',
        (req) => {
          req.headers['x-custom-headers'] = 'added by cy.intercept';
          req.reply({
            statusCode: 200,
            statusMessage: "OK",
            headers: {
              "x-custom-header": 'mgunn',
            },
            body: {
              jobId: jobId
            }
          });
        }
    )
      .as('createJob');


    cy.intercept('HEAD', 'https://gtfs-validator-results.mobilitydata.org/_waiting_/report.html')
      .as('awaitJob');

    cy.intercept('HEAD', `https://gtfs-validator-results.mobilitydata.org/${jobId}/report.html`)
      .as('resultJob');


    // Enter URL to .zip file
    cy.get('input#url')
      .type(url);

    // Choose Region - US
    cy.get('select#region')
      // .select('United States');
      .select(1);

    // Submit
    cy.get('button[type=submit]')
      .click();

    // Wait for submission

    cy.wait('@createJob').its('response.statusCode').should('eq', 200);

    // cy.wait('@awaitJob').its('response.statusCode').should('eq', 404);

    // cy.wait('@resultJob', { timeout: 20000 }).its('response.statusCode').should('eq', 200);

    // cy.get('dialog')
    //   .should('be.visible')
    //   .within(() => {
    //     cy.get('a.btn:contains("Open Report")').should('be.visible');
    //   });





  });
});
