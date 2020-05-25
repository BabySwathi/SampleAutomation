Feature: Projects in demo application

  Scenario:Registering to Insurance Project in guru99DemoApplication
    Given User is in Demo application and able to access Insurance project
    Then User should register by providing all the necessary details and Verify it
      |Miss|Swathi|Vedamoorthy|9162564532|1995|June|1|2|Engineer|New street|Chennai|Tamil Nadu|671234|User|User@123|

  Scenario:Login to NewTours and Verify the MainMenu
    Given User is in Demo application and able to access NewTours
    Then User should verify the webtable datas

