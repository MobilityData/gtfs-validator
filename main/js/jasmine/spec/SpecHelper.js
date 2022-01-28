beforeEach(function () {
  jasmine.addMatchers({
    hasAtLeastThisManyChildren: function ()  {
      return {
        compare: function (actual, expected) {
          var vnode = actual;
          return {
            pass: vnode.children.length >= expected
          };
        }
      };
    },

  });
});
