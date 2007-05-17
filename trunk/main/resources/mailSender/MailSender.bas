Attribute VB_Name = "MailSender"
Public Sub Main()
    Dim app As New Excel.Application
    Dim fso As New Scripting.FileSystemObject
    Dim f As Scripting.TextStream
    Dim orders As String
    Dim a_strArgs() As String
    a_strArgs = Split(Command$, " ")
    app.DisplayAlerts = False
    Set f = fso.OpenTextFile(a_strArgs(2))
    orders = f.ReadAll
    f.Close
    
    Dim newBook As Excel.Workbook
    Set newBook = app.Workbooks.Add
    With newBook
        .Title = a_strArgs(1)
        .Subject = a_strArgs(1)
        .SaveAs FileName:=a_strArgs(2), FileFormat:=xlTextPrinter
    End With
    
    
    'app.Workbooks.Open a_strArgs(2)
    Clipboard.Clear
    Clipboard.SetText orders
    
    Dim s As Excel.Worksheet
    Set s = newBook.ActiveSheet
    s.Range("A1").Select
    s.Paste
    'app.Selection.PasteSpecial Paste:=xlValues
    newBook.Save
    
    app.ActiveWorkbook.SendMail a_strArgs(0), a_strArgs(1)
    app.Quit
End Sub
