Feature: Data Table example two


  @Datatabletwo
  Scenario: Data Table example two

    Given I open the application "https://practicesoftwaretesting.com/"

    Then click on the following:
      | Fields | Xpath |
      | Home   |       |

Then wait for the element
#
#
#    Then choose the option "Hand Tools" from the dropdown "Categories"
#
#
    Then click on the following:
      | Fields             | Xpath |
      | Combination Pliers |       |
      | Add to favourites  |       |
      | Add to cart        |       |
      | cart               |       |
      | btn btn-danger     |       |
#
#
#
    Then click on the following:
      | Fields | Xpath |
      | Home   |       |
      | Bolt Cutters |       |
      | Add to favourites  |       |
      | Add to cart        |       |
      | cart               |       |
      | btn btn-danger     |       |
      | Home   |       |
