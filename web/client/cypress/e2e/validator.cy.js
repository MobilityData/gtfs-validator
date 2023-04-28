/// <reference types="cypress" />

context('GTFS Validator - Core Workflow', () => {
  let numReportCalls = 0;

  beforeEach(() => {
    cy.visit('https://gtfs-validator.mobilitydata.org/')
  });

  it('Validate GTFS .zip file', () => {
    const url = 'https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip';
    const jobId = '8f6be6fb-1fee-41f8-b401-b2b4b552e177';

    // Setup intercept aliases
    cy.intercept(
        'POST',
        'https://*/create-job',
        (req) => {
          req.reply({
            statusCode: 200,
            statusMessage: "OK",
            body: {
              jobId: jobId
            }
          });
        }
      )
      .as('createJob');


    cy.intercept(
      'HEAD',
      'https://gtfs-validator-results.mobilitydata.org/*/report.html',
      (req) => {
        if (numReportCalls > 1) {
          // return 200 code
          req.reply({
            statusCode: 200,
            headers: {
              "x-number-of-calls": numReportCalls.toString()
            }
          });
        } else {
          // return 404 code
          req.reply({
            statusCode: 404,
            headers: {
              "x-number-of-calls": numReportCalls.toString()
            }
          });
        }
        numReportCalls++;
      })
      .as('awaitJob');


    // Enter URL to .zip file
    cy.get('input#url')
      .type(url);

    // Choose Region - US
    cy.get('select#region')
      .select(1);

    // Submit
    cy.get('button[type=submit]')
      .click();

    // Wait for create job 200 response
    cy.wait('@createJob').should((xhr) => {
      expect(xhr.response.statusCode).to.eq(200);
    });

    // Wait for 404
    cy.wait('@awaitJob').should((xhr) => {
      expect(xhr.response.statusCode).to.eq(404);
      expect(xhr.request.url).to.contain('_waiting_');
      expect(xhr.request.url).to.not.contain(jobId);
    });

    // Wait for 404
    cy.wait('@awaitJob').should((xhr) => {
      expect(xhr.response.statusCode).to.eq(404);
      expect(xhr.request.url).to.contain(jobId);
      expect(xhr.request.url).to.not.contain('_waiting_');
    });

    // Wait for 200
    cy.wait('@awaitJob').should((xhr) => {
      expect(xhr.response.statusCode).to.eq(200);
      expect(xhr.request.url).to.contain(jobId);
      expect(xhr.request.url).to.not.contain('_waiting_');
    });

    // Confirm "report ready"
    cy.get('dialog')
      .should('be.visible')
      .within(() => {
        cy.get('a.btn:contains("Open Report")').should('be.visible');
      });
  });
});
