echo on
for /r %%i in (*) do (if %%i=="*.java" (javac -c "/bin" %%i))
jar -cfe CatChat_Server.jar catchat.client.ServerMain "bin/*.class"