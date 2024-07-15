package com.gawkgawk.util

/**
 *  This utility function is useful for handling file uploads,
 *  ensuring that each uploaded file has a unique name
 *  to prevent overwriting existing files.
 * 1. Generates a unique file name using a UUID and the original file's extension.
 * 2. Reads the file's bytes from the provided input stream.
 * 3. Ensures that the specified folder path exists, creating directories as necessary.
 * 4. Writes the bytes to a new file with the generated name in the specified folder.
 * 5. Returns the name of the saved file.
 */
import io.ktor.http.content.*
import java.io.File
import java.util.*
// originalFileName represents the name of the uploaded file as it was on the client's file system.
// This property is of type String?
fun PartData.FileItem.saveFile(folderPath: String): String{
    // UUID.randomUUID(): Generates a unique identifier.
    // Casts the originalFileName to a String.
    val fileName = "${UUID.randomUUID()}.${File(originalFileName as String).extension}"
    //streamProvider(): A method of PartData.FileItem that
    // provides an *input stream* for reading the file's content.
    // For e.g: Read the content of photo.jpg into a byte array.
    val fileBytes = streamProvider().readBytes()

    // folder.mkdirs(): Creates the directory named by this File,
    // including any necessary but nonexistent parent directories.
    val folder = File(folderPath)
    folder.mkdirs()

    // File("$folder/$fileName"): Creates a File object representing the file to be saved.
    // writeBytes(fileBytes): Writes the byte array to the file.
    File("$folder/$fileName").writeBytes(fileBytes)
    return fileName
}