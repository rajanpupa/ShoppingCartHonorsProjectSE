# ShoppingCartHonorsProjectSE
Shopping cart project

The project is a simple shopping card (Desktop version) where mysql is used as a backend database in the same computer.

## Steps of engineering a software
* Requirements Analysis/elicitation
* Use cases
* Architectural Analysis
* Use case analysis
* Architectural design
* usecase design
* Class diagram design
* subsystem design

The purpose of the project is to learn all the steps for developing a project using software engineering techniques(Rational unified process)
## First the use cases are Designed
* user can log in
* privileged user can manage(add, delete, edit) catalogs
* privileged user can manage(add, delete, edit) products
* user can browse the products
* user can add product to cart
* user can checkout
* place order

## The Sequence diagram 
For the second step, a sequence diagram is created for each flow of the use case, along with activation bars and sequence numbers. eg.
![ScreenShot](https://lh6.googleusercontent.com/-CZQerFXo1uk/VTsLAA2oIfI/AAAAAAAACjE/9mufd19q-5w/w672-h577-no/ManageProductCatalog.jpg)
 
 The collaboration diagrams are created from the sequence diagram
 ![ScreenShot](https://lh5.googleusercontent.com/-mkPTJsG-Lgo/VTsMkF5KFoI/AAAAAAAACjU/mXC02da5Plg/w633-h577-no/ManageProductCatalog%2B-%2BCommunications.jpg)
 
 Then the VOPC(View of participating classes) or the class diagram. 
 
 After that, we have brief idea of the classes required, subsystems, interactions, facades service methods for each subsystems etc.
 
 ##This project is an example of the above analysis and design. These are the steps
 * The top level application components are the GUI classes.
 * The gui classes take the event handlers from the UI-controllers where the event is handeled.
 * There are *Data classes which maintains the data to be displayed in the GUI. These classes also talk to the Use Case Controllers to synchronize data to the database in case of add,delete or Edit.
 * The next classes are the Controllers(use case controllers) which take request from GUI, UI-Controllers and Data classes and take help of Subsystems to do things.
 * There are external interfaces which are composition of generic interfaces used throughout the application..
 * There are different subsystems (eg manageProduct, Order, ShoppingCart etc) which are realized by corresponding Facade.
