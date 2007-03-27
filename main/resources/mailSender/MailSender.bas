Attribute VB_Name = "MailSender"
Public Sub Main()
    Dim app As New Excel.Application
    Dim a_strArgs() As String
    a_strArgs = Split(Command$, " ")
    app.Workbooks.Open a_strArgs(2)
    app.ActiveWorkbook.SendMail a_strArgs(0), a_strArgs(1)
    app.Quit
End Sub
