package com.zaxcler.baselib.utils


import android.content.ContentUris
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import android.graphics.Bitmap
import java.io.FileOutputStream
/**
 * 类描述：
 * 作者: Created by zaxcler on 2019/6/15.
 * 代码版本: 1.0
 * 邮箱: 610529094@qq.com
 */



fun getFilePath(context: Context, uri: Uri): String? {

    if ("content".equals(uri.scheme, ignoreCase = true)) {

        val sdkVersion = Build.VERSION.SDK_INT
        return if (sdkVersion >= 19) { // api >= 19
            getRealPathFromUriAboveApi19(context, uri)
        } else { // api < 19
            getRealPathFromUriBelowAPI19(context, uri)
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }

    return null
}

/**
 * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
 *
 * @param context 上下文对象
 * @param uri     图片的Uri
 * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
 */
private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
    return getDataColumn(context, uri, null, null)
}

/**
 * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
 * @return
 */
private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
    var path: String? = null

    val projection = arrayOf(MediaStore.Images.Media.DATA)
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
            path = cursor.getString(columnIndex)
        }
    } catch (e: Exception) {
        if (cursor != null) {
            cursor.close()
        }
    }

    return path
}


/**
 * 适配api19及以上,根据uri获取图片的绝对路径
 *
 * @param context 上下文对象
 * @param uri     图片的Uri
 * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
 */
@SuppressLint("NewApi")
private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
    var filePath: String? = null
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // 如果是document类型的 uri, 则通过document id来进行处理
        val documentId = DocumentsContract.getDocumentId(uri)
        if (isMediaDocument(uri)) { // MediaProvider
            // 使用':'分割
            val id = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val selection = MediaStore.Images.Media._ID + "=?"
            val selectionArgs = arrayOf(id)
            filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs)
        } else if (isDownloadsDocument(uri)) { // DownloadsProvider
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(documentId))
            filePath = getDataColumn(context, contentUri, null, null)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        // 如果是 content 类型的 Uri
        filePath = getDataColumn(context, uri, null, null)
    } else if ("file" == uri.scheme) {
        // 如果是 file 类型的 Uri,直接获取图片对应的路径
        filePath = uri.path
    }
    return filePath
}

/**
 * @param uri the Uri to check
 * @return Whether the Uri authority is MediaProvider
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri the Uri to check
 * @return Whether the Uri authority is DownloadsProvider
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}


/**
 * 获取文件大小
 * */

const val SIZE_TYPE_B = 1//获取文件大小单位为B的double值
const val SIZE_TYPE_KB = 2//获取文件大小单位为KB的double值
const val SIZE_TYPE_MB = 3//获取文件大小单位为MB的double值
const val SIZE_TYPE_GB = 4//获取文件大小单位为GB的double值
/**
 * 获取指定文件或指定文件夹的的指定单位的大小
 * @param filePath 文件路径
 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
 * @return double值的大小
 */
fun getFolderOrFileSize(filePath: String, sizeType: Int): Double {
    val file = File(filePath)
    var blockSize: Long = 0
    try {
        if (file.isDirectory) {
            blockSize = getFolderSize(file)
        } else {
            blockSize = getFileSize(file)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("获取文件大小", "获取失败!")
    }

    return formatFileSize(blockSize, sizeType)
}

/**
 * 调用此方法自动计算指定文件或指定文件夹的大小
 * @param filePath 文件路径
 * @return 计算好的带B、KB、MB、GB的字符串
 */
fun getAutoFolderOrFileSize(filePath: String): String {
    val file = File(filePath)
    var blockSize: Long = 0
    try {
        if (file.isDirectory) {
            blockSize = getFolderSize(file)
        } else {
            blockSize = getFileSize(file)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("获取文件大小", "获取失败!")
    }

    return formatFileSize(blockSize)
}

/**
 * 获取指定文件的大小
 * @param file
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
private fun getFileSize(file: File): Long {
    var size: Long = 0
    if (file.exists()) {
        var fis: FileInputStream? = null
        fis = FileInputStream(file)
        size = fis.available().toLong()
        fis.close()
    } else {
        file.createNewFile()
        Log.e("获取文件大小", "文件不存在!")
    }

    return size
}

/**
 * 获取指定文件夹的大小
 * @param file
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
private fun getFolderSize(file: File): Long {
    var size: Long = 0
    val flist = file.listFiles()
    for (i in flist.indices) {
        size += if (flist[i].isDirectory) {
            getFolderSize(flist[i])
        } else {
            getFileSize(flist[i])
        }
    }
    return size
}

/**
 * 转换文件大小
 * @param fileSize
 * @return
 */
private fun formatFileSize(fileSize: Long): String {
    val df = DecimalFormat("#.00")
    var fileSizeString = ""
    val wrongSize = "0B"
    if (fileSize == 0L) {
        return wrongSize
    }
    if (fileSize < 1024) {
        fileSizeString = df.format(fileSize.toDouble()) + "B"
    } else if (fileSize < 1048576) {
        fileSizeString = df.format(fileSize.toDouble() / 1024) + "KB"
    } else if (fileSize < 1073741824) {
        fileSizeString = df.format(fileSize.toDouble() / 1048576) + "MB"
    } else {
        fileSizeString = df.format(fileSize.toDouble() / 1073741824) + "GB"
    }
    return fileSizeString
}

/**
 * 转换文件大小,指定转换的类型
 * @param fileSize
 * @param sizeType
 * @return
 */
private fun formatFileSize(fileSize: Long, sizeType: Int): Double {
    val df = DecimalFormat("#.00")
    var fileSizeLong = 0.0
    when (sizeType) {
        SIZE_TYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileSize.toDouble()))
        SIZE_TYPE_KB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileSize.toDouble() / 1024))
        SIZE_TYPE_MB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileSize.toDouble() / 1048576))
        SIZE_TYPE_GB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileSize.toDouble() / 1073741824))
        else -> {
        }
    }
    return fileSizeLong
}


fun saveBitmap2Loacl(bitmap: Bitmap,dir:String,name:String):Boolean{
    var success = true
    try {
        val dirPath = File(dir)
        if (!dirPath.exists()){
            dirPath.mkdirs()
        }
        val file = File(dir +File.separator+ name)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        success = true
    } catch (e: Exception) {
        e.printStackTrace()
        success =  false
    }finally {
        return success
    }
}
