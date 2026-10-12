package com.example.ui.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat

object ShareHelper {

    /**
     * Resolves the host Activity from any Context or ContextWrapper.
     */
    fun findActivity(context: Context): Activity? {
        var current = context
        while (current is ContextWrapper) {
            if (current is Activity) {
                return current
            }
            current = current.baseContext
        }
        return null
    }

    /**
     * Launches external applications using ShareCompat.IntentBuilder for message content sharing.
     * Guarantees that even if external apps are absent or the chooser fails to launch,
     * the text is safely preserved in the system clipboard so the user is never left hanging.
     */
    fun shareText(
        context: Context,
        content: String,
        subject: String = "⚠️ Scam Alert: Verified by ScamShield AI",
        chooserTitle: String = "Share Scam Alert via..."
    ): Boolean {
        // Step 1: Always copy to clipboard as an immediate guarantee so content is never lost
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("ScamShield Alert", content))
        } catch (_: Exception) {}

        // Step 2: Attempt standard ShareCompat launch
        val activity = findActivity(context)

        return try {
            if (activity != null) {
                val intent = ShareCompat.IntentBuilder(activity)
                    .setType("text/plain")
                    .setSubject(subject)
                    .setText(content)
                    .setChooserTitle(chooserTitle)
                    .createChooserIntent()
                    .apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                activity.startActivity(intent)
                true
            } else {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, content)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(sendIntent, chooserTitle).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                true
            }
        } catch (e: Exception) {
            // Step 3: Fallback - Attempt direct ACTION_SEND without chooser
            try {
                val directIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, content)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(directIntent)
                true
            } catch (_: Exception) {
                // Clipboard already holds the text; return true because user's share action succeeded
                false
            }
        }
    }

    /**
     * Launches WhatsApp directly with pre-filled warning text if installed,
     * or falls back to system ShareCompat chooser.
     */
    fun shareToWhatsApp(
        context: Context,
        content: String,
        fallbackChooserTitle: String = "Share Scam Warning"
    ): Boolean {
        return try {
            val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = "com.whatsapp"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(whatsappIntent)
            true
        } catch (_: Exception) {
            shareText(context, content, chooserTitle = fallbackChooserTitle)
        }
    }

    /**
     * Launches SMS app directly with pre-filled warning text.
     */
    fun shareToSms(
        context: Context,
        content: String
    ): Boolean {
        return try {
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:")
                putExtra("sms_body", content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(smsIntent)
            true
        } catch (_: Exception) {
            shareText(context, content)
        }
    }

    /**
     * Launches external applications to share files (e.g. CSV exports) using ShareCompat.IntentBuilder.
     */
    fun shareFile(
        context: Context,
        fileUri: Uri,
        mimeType: String = "text/csv",
        subject: String = "ScamShield AI History Export",
        chooserTitle: String = "Export Analysis History via..."
    ): Boolean {
        return try {
            val activity = findActivity(context)
            if (activity != null) {
                val intent = ShareCompat.IntentBuilder(activity)
                    .setType(mimeType)
                    .setSubject(subject)
                    .setStream(fileUri)
                    .setChooserTitle(chooserTitle)
                    .createChooserIntent()
                    .apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                activity.startActivity(intent)
                true
            } else {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(intent, chooserTitle).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Extracts shared message content from an incoming Intent using ShareCompat.IntentReader.
     */
    fun extractIncomingText(activity: Activity): String? {
        return try {
            val reader = ShareCompat.IntentReader(activity)
            if (reader.isShareIntent) {
                val text = reader.text?.toString()
                val subject = reader.subject
                when {
                    !text.isNullOrBlank() -> text
                    !subject.isNullOrBlank() -> subject
                    else -> null
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
