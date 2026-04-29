package com.phantomfiles.pro.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.phantomfiles.pro.data.model.FilesCache;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FilesCacheDao_Impl implements FilesCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FilesCache> __insertionAdapterOfFilesCache;

  private final SharedSQLiteStatement __preparedStmtOfClearDirectory;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public FilesCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFilesCache = new EntityInsertionAdapter<FilesCache>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `files_cache` (`path`,`name`,`size`,`type`,`modified`,`hash`,`partition`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FilesCache entity) {
        statement.bindString(1, entity.getPath());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getSize());
        statement.bindString(4, entity.getType());
        statement.bindLong(5, entity.getModified());
        statement.bindString(6, entity.getHash());
        statement.bindString(7, entity.getPartition());
      }
    };
    this.__preparedStmtOfClearDirectory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM files_cache WHERE path LIKE ? || '%'";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM files_cache";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<FilesCache> files,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFilesCache.insert(files);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDirectory(final String parentPath,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDirectory.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, parentPath);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDirectory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FilesCache>> getFilesInDirectory(final String parentPath) {
    final String _sql = "SELECT * FROM files_cache WHERE path LIKE ? || '/%' AND path NOT LIKE ? || '/%/%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, parentPath);
    _argIndex = 2;
    _statement.bindString(_argIndex, parentPath);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"files_cache"}, new Callable<List<FilesCache>>() {
      @Override
      @NonNull
      public List<FilesCache> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "path");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfModified = CursorUtil.getColumnIndexOrThrow(_cursor, "modified");
          final int _cursorIndexOfHash = CursorUtil.getColumnIndexOrThrow(_cursor, "hash");
          final int _cursorIndexOfPartition = CursorUtil.getColumnIndexOrThrow(_cursor, "partition");
          final List<FilesCache> _result = new ArrayList<FilesCache>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FilesCache _item;
            final String _tmpPath;
            _tmpPath = _cursor.getString(_cursorIndexOfPath);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpSize;
            _tmpSize = _cursor.getLong(_cursorIndexOfSize);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpModified;
            _tmpModified = _cursor.getLong(_cursorIndexOfModified);
            final String _tmpHash;
            _tmpHash = _cursor.getString(_cursorIndexOfHash);
            final String _tmpPartition;
            _tmpPartition = _cursor.getString(_cursorIndexOfPartition);
            _item = new FilesCache(_tmpPath,_tmpName,_tmpSize,_tmpType,_tmpModified,_tmpHash,_tmpPartition);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object search(final String query,
      final Continuation<? super List<FilesCache>> $completion) {
    final String _sql = "SELECT * FROM files_cache WHERE name LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FilesCache>>() {
      @Override
      @NonNull
      public List<FilesCache> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "path");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfModified = CursorUtil.getColumnIndexOrThrow(_cursor, "modified");
          final int _cursorIndexOfHash = CursorUtil.getColumnIndexOrThrow(_cursor, "hash");
          final int _cursorIndexOfPartition = CursorUtil.getColumnIndexOrThrow(_cursor, "partition");
          final List<FilesCache> _result = new ArrayList<FilesCache>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FilesCache _item;
            final String _tmpPath;
            _tmpPath = _cursor.getString(_cursorIndexOfPath);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpSize;
            _tmpSize = _cursor.getLong(_cursorIndexOfSize);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpModified;
            _tmpModified = _cursor.getLong(_cursorIndexOfModified);
            final String _tmpHash;
            _tmpHash = _cursor.getString(_cursorIndexOfHash);
            final String _tmpPartition;
            _tmpPartition = _cursor.getString(_cursorIndexOfPartition);
            _item = new FilesCache(_tmpPath,_tmpName,_tmpSize,_tmpType,_tmpModified,_tmpHash,_tmpPartition);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object findByHash(final String hash,
      final Continuation<? super List<FilesCache>> $completion) {
    final String _sql = "SELECT * FROM files_cache WHERE hash = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, hash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FilesCache>>() {
      @Override
      @NonNull
      public List<FilesCache> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "path");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfModified = CursorUtil.getColumnIndexOrThrow(_cursor, "modified");
          final int _cursorIndexOfHash = CursorUtil.getColumnIndexOrThrow(_cursor, "hash");
          final int _cursorIndexOfPartition = CursorUtil.getColumnIndexOrThrow(_cursor, "partition");
          final List<FilesCache> _result = new ArrayList<FilesCache>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FilesCache _item;
            final String _tmpPath;
            _tmpPath = _cursor.getString(_cursorIndexOfPath);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpSize;
            _tmpSize = _cursor.getLong(_cursorIndexOfSize);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final long _tmpModified;
            _tmpModified = _cursor.getLong(_cursorIndexOfModified);
            final String _tmpHash;
            _tmpHash = _cursor.getString(_cursorIndexOfHash);
            final String _tmpPartition;
            _tmpPartition = _cursor.getString(_cursorIndexOfPartition);
            _item = new FilesCache(_tmpPath,_tmpName,_tmpSize,_tmpType,_tmpModified,_tmpHash,_tmpPartition);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
