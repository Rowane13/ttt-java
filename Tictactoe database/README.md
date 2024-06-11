#Tic tac toe

## Database Setup Instructions

Follow these steps to recreate the database and connection.

### Prerequisites

- MySQL installed on your machine
- MySQL Workbench installed on your machine (optional, but recommended)

### Step 1: Import the Database Schema and Data

1. Open MySQL Workbench and connect to your MySQL server.
2. Navigate to `Server` > `Data Import`.
3. Select the option `Import from Self-Contained File`.
4. Browse and select the `DB.sql` file from this repository.
5. Choose `New` in the `Default Schema to be Imported To` section and name it `TicTacToeDB`.
6. Click `Start Import` to begin the process.

### Step 2: Configure the Database Connection

1. Open the `config/database.properties` file.
2. Update the connection details if necessary (e.g., username and password):
   ```properties
   db.url=jdbc:mysql://localhost:3306/TicTacToeDB
   db.username=root
   db.password=Rowane21