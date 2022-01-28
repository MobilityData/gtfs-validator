describe("View", function() {
  var v;

  beforeEach(function() {
    v = ReportView.view()
    console.log("v", v)
  });

  it("should return a vnode with a div", function() {
    expect(v.sel).toEqual("div");
  });

  it("should have 1 or more children", function() {
    expect(v.children.length).toBeGreaterThan(0);
  });

  it("should have 2 or more children (via helper check)", function() {
    expect(v).hasAtLeastThisManyChildren(2)
  });

  it("should have the same number of children as there are notices", function() {
    expect(v.children.length).toEqual(report.notices.length);
  });

  it("should have the same number of children as there are showNotice elements", function() {
    expect(v.children.length).toEqual(ReportView.showNotice.length)
  });
});

describe("Notice Details", function() {
  var v;

  beforeEach(function() {
    // notice = { hello: "world"}
    notice = { 
      hello: "world",
      sampleNotices: [{}],
    }
    // console.log("notice", notice)
  });

  it("should render a genericCodeView", function() {
    expect(NoticeDetails.genericCodeView(notice).sel).toEqual("div");
  });

  let codes = Object.keys(NoticeDetails.codeView);
  for (let i = 0; i < codes.length; i++) {
    let code = codes[i];
    it(`should render a codeview for "${code}"`, function() {
      expect(["div", "ul", "p"]).toContain(NoticeDetails.codeView[code](notice).sel);
    });
  }

});


