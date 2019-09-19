using System.IO;

namespace TesterTools
{ 
    static class FileManager
    {
        public static void ClearDir(string dirPath)
        {
            DirectoryInfo di = new DirectoryInfo(dirPath);

            foreach (FileInfo file in di.GetFiles())
            {
                file.Delete();
            }

            foreach (DirectoryInfo dir in di.GetDirectories())
            {
                dir.Delete(true);
            }
        }

        public static void WriteToFile(string file, string line, bool? newLineBegin, bool semicolon)
        {
            string tmp = line += (semicolon ? ";" : "");

            if (newLineBegin != null)
                tmp = (bool)newLineBegin ? "\n" + line + (semicolon ? ";" : "") : line + (semicolon ? ";\n" : "\n");

            using (var tw = new StreamWriter(file, true))
                tw.Write(line);
        }
    }
}