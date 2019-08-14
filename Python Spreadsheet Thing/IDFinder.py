import gspread
import pprint
from oauth2client.service_account import ServiceAccountCredentials

scope = ['https://spreadsheets.google.com/feeds','https://www.googleapis.com/auth/drive']

creds = ServiceAccountCredentials.from_json_keyfile_name('roboticsOrganizing.json', scope)

client = gspread.authorize(creds)

sheet= client.open('Robotics_ID').sheet1

#dataID = sheet.get_all_records()
pp = pprint.PrettyPrinter()

exitFlag = False

while exitFlag == False:

    selection = input('Would You Like To Find Something or Make Something New? (Find, New) ')

    if selection.casefold() in ['find', 'Find']:
        selection = input("What Do You Need To Find? (Name, CanID, PortNum, IPAddress) ")

        if selection.casefold() in ['canid', 'CanID']:
            canIDselect = input("What CanID are you looking for? ")
            try:
                cell = sheet.find(canIDselect)
                print("Found Something At R%sC%s" % (cell.row, cell.col))
                values_list = sheet.row_values(cell.row)
                print(values_list)
            except gspread.exceptions.CellNotFound:
                print("Could Not Find Anything, Sorry!")

        elif selection.casefold() in ['portnum', 'PortNum']:
            portNumselect = input('What Port Number Are You Looking For? ')
            try:
                cell = sheet.find(portNumselect)
                print('Found Something At Row %s Column %s' % (cell.row, cell.col))
                values_list = sheet.row_values(cell.row)
                print(values_list)
            except gspread.exceptions.CellNotFound:
                print("Could Not Find Anything, Sorry!")

        elif selection.casefold() in ['ip', "IP"]:
            IPNumselect = input('What IP Address Are You Looking For? ')
            try:
                cell = sheet.find(IPNumselect)
                print('Found Something At Row %s Column %s' % (cell.row, cell.col))
                values_list = sheet.row_values(cell.row)
                print(values_list)
            except gspread.exceptions.CellNotFound:
                print("Could Not Find Anything, Sorry!")

        elif selection.casefold() in ['name', "Name"]:
            nameSelect = input('What Name Are You Looking For? ')
            try:
                cell = sheet.find(nameSelect)
                print('Found Something At Row %s Column %s' % (cell.row, cell.col))
                values_list = sheet.row_values(cell.row)
                print(values_list)
            except gspread.exceptions.CellNotFound:
                print("Could Not Find Anything, Sorry!")

        else:
            exit()

    elif selection.casefold() in ['new', 'New']:
        newName = input("What is the name of the new object? ")
        newPort = input("What is the port id? ")
        newIP = input("What is the IP, put N/A if not possible? ")
        newCANID = input("What is the CAN ID of the object? ")

        try:
            cell = sheet.find(newName)
            print("There is already an object with the same name")

        except gspread.exceptions.CellNotFound:
            try:
                cell = sheet.find(newPort)
                print("There is already a object with the same port")
            except gspread.exceptions.CellNotFound:
                try:
                    if newIP.casefold() in ['n/a', 'na']:
                        raise gspread.exceptions.CellNotFound
                    else:
                        cell = sheet.find(newIP)
                        print("There is already an object with this IP")
                except gspread.exceptions.CellNotFound:
                    try:
                        cell = sheet.find(newCANID)
                        print("There is already an object with this CAN ID")
                    except gspread.exceptions.CellNotFound:
                        print("No repeats found, adding new object...")

                        row = [newName,newPort,newIP,newCANID]
                        index = 2
                        sheet.insert_row(row, index)






    else:
        exit()
