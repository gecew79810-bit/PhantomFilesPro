package com.phantomfiles.pro.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PhantomDatabase_Impl extends PhantomDatabase {
  private volatile FilesCacheDao _filesCacheDao;

  private volatile RecycleBinDao _recycleBinDao;

  private volatile VaultDao _vaultDao;

  private volatile ScanResultDao _scanResultDao;

  private volatile BookmarkDao _bookmarkDao;

  private volatile OperationLogDao _operationLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `files_cache` (`path` TEXT NOT NULL, `name` TEXT NOT NULL, `size` INTEGER NOT NULL, `type` TEXT NOT NULL, `modified` INTEGER NOT NULL, `hash` TEXT NOT NULL, `partition` TEXT NOT NULL, PRIMARY KEY(`path`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recycle_bin` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `originalPath` TEXT NOT NULL, `recyclePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `fileSize` INTEGER NOT NULL, `mimeType` TEXT, `isDirectory` INTEGER NOT NULL, `deletedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vault_files` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `encryptedName` TEXT NOT NULL, `originalName` TEXT NOT NULL, `originalPath` TEXT NOT NULL, `fileSize` INTEGER NOT NULL, `mimeType` TEXT, `addedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scan_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `scanType` TEXT NOT NULL, `foundCount` INTEGER NOT NULL, `sizeBytes` INTEGER NOT NULL, `scannedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `path` TEXT NOT NULL, `name` TEXT NOT NULL, `iconColor` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `operations_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `operation` TEXT NOT NULL, `sourcePath` TEXT NOT NULL, `destPath` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `status` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '82898f9c3837313763c308d4b9d58fec')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `files_cache`");
        db.execSQL("DROP TABLE IF EXISTS `recycle_bin`");
        db.execSQL("DROP TABLE IF EXISTS `vault_files`");
        db.execSQL("DROP TABLE IF EXISTS `scan_results`");
        db.execSQL("DROP TABLE IF EXISTS `bookmarks`");
        db.execSQL("DROP TABLE IF EXISTS `operations_log`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFilesCache = new HashMap<String, TableInfo.Column>(7);
        _columnsFilesCache.put("path", new TableInfo.Column("path", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("size", new TableInfo.Column("size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("modified", new TableInfo.Column("modified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("hash", new TableInfo.Column("hash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFilesCache.put("partition", new TableInfo.Column("partition", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFilesCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFilesCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFilesCache = new TableInfo("files_cache", _columnsFilesCache, _foreignKeysFilesCache, _indicesFilesCache);
        final TableInfo _existingFilesCache = TableInfo.read(db, "files_cache");
        if (!_infoFilesCache.equals(_existingFilesCache)) {
          return new RoomOpenHelper.ValidationResult(false, "files_cache(com.phantomfiles.pro.data.model.FilesCache).\n"
                  + " Expected:\n" + _infoFilesCache + "\n"
                  + " Found:\n" + _existingFilesCache);
        }
        final HashMap<String, TableInfo.Column> _columnsRecycleBin = new HashMap<String, TableInfo.Column>(8);
        _columnsRecycleBin.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("originalPath", new TableInfo.Column("originalPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("recyclePath", new TableInfo.Column("recyclePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("fileName", new TableInfo.Column("fileName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("fileSize", new TableInfo.Column("fileSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("mimeType", new TableInfo.Column("mimeType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("isDirectory", new TableInfo.Column("isDirectory", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecycleBin.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecycleBin = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecycleBin = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecycleBin = new TableInfo("recycle_bin", _columnsRecycleBin, _foreignKeysRecycleBin, _indicesRecycleBin);
        final TableInfo _existingRecycleBin = TableInfo.read(db, "recycle_bin");
        if (!_infoRecycleBin.equals(_existingRecycleBin)) {
          return new RoomOpenHelper.ValidationResult(false, "recycle_bin(com.phantomfiles.pro.data.model.RecycleBinItem).\n"
                  + " Expected:\n" + _infoRecycleBin + "\n"
                  + " Found:\n" + _existingRecycleBin);
        }
        final HashMap<String, TableInfo.Column> _columnsVaultFiles = new HashMap<String, TableInfo.Column>(7);
        _columnsVaultFiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("encryptedName", new TableInfo.Column("encryptedName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("originalName", new TableInfo.Column("originalName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("originalPath", new TableInfo.Column("originalPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("fileSize", new TableInfo.Column("fileSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("mimeType", new TableInfo.Column("mimeType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVaultFiles.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVaultFiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVaultFiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVaultFiles = new TableInfo("vault_files", _columnsVaultFiles, _foreignKeysVaultFiles, _indicesVaultFiles);
        final TableInfo _existingVaultFiles = TableInfo.read(db, "vault_files");
        if (!_infoVaultFiles.equals(_existingVaultFiles)) {
          return new RoomOpenHelper.ValidationResult(false, "vault_files(com.phantomfiles.pro.data.model.VaultFile).\n"
                  + " Expected:\n" + _infoVaultFiles + "\n"
                  + " Found:\n" + _existingVaultFiles);
        }
        final HashMap<String, TableInfo.Column> _columnsScanResults = new HashMap<String, TableInfo.Column>(5);
        _columnsScanResults.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("scanType", new TableInfo.Column("scanType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("foundCount", new TableInfo.Column("foundCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("sizeBytes", new TableInfo.Column("sizeBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanResults.put("scannedAt", new TableInfo.Column("scannedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScanResults = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScanResults = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScanResults = new TableInfo("scan_results", _columnsScanResults, _foreignKeysScanResults, _indicesScanResults);
        final TableInfo _existingScanResults = TableInfo.read(db, "scan_results");
        if (!_infoScanResults.equals(_existingScanResults)) {
          return new RoomOpenHelper.ValidationResult(false, "scan_results(com.phantomfiles.pro.data.model.ScanResult).\n"
                  + " Expected:\n" + _infoScanResults + "\n"
                  + " Found:\n" + _existingScanResults);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarks = new HashMap<String, TableInfo.Column>(4);
        _columnsBookmarks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("path", new TableInfo.Column("path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("iconColor", new TableInfo.Column("iconColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarks = new TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks);
        final TableInfo _existingBookmarks = TableInfo.read(db, "bookmarks");
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmarks(com.phantomfiles.pro.data.model.Bookmark).\n"
                  + " Expected:\n" + _infoBookmarks + "\n"
                  + " Found:\n" + _existingBookmarks);
        }
        final HashMap<String, TableInfo.Column> _columnsOperationsLog = new HashMap<String, TableInfo.Column>(6);
        _columnsOperationsLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOperationsLog.put("operation", new TableInfo.Column("operation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOperationsLog.put("sourcePath", new TableInfo.Column("sourcePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOperationsLog.put("destPath", new TableInfo.Column("destPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOperationsLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOperationsLog.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOperationsLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOperationsLog = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOperationsLog = new TableInfo("operations_log", _columnsOperationsLog, _foreignKeysOperationsLog, _indicesOperationsLog);
        final TableInfo _existingOperationsLog = TableInfo.read(db, "operations_log");
        if (!_infoOperationsLog.equals(_existingOperationsLog)) {
          return new RoomOpenHelper.ValidationResult(false, "operations_log(com.phantomfiles.pro.data.model.OperationLog).\n"
                  + " Expected:\n" + _infoOperationsLog + "\n"
                  + " Found:\n" + _existingOperationsLog);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "82898f9c3837313763c308d4b9d58fec", "207cf439b9d5d4eb60d23203a87aed2e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "files_cache","recycle_bin","vault_files","scan_results","bookmarks","operations_log");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `files_cache`");
      _db.execSQL("DELETE FROM `recycle_bin`");
      _db.execSQL("DELETE FROM `vault_files`");
      _db.execSQL("DELETE FROM `scan_results`");
      _db.execSQL("DELETE FROM `bookmarks`");
      _db.execSQL("DELETE FROM `operations_log`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FilesCacheDao.class, FilesCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecycleBinDao.class, RecycleBinDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VaultDao.class, VaultDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScanResultDao.class, ScanResultDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkDao.class, BookmarkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OperationLogDao.class, OperationLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FilesCacheDao filesCacheDao() {
    if (_filesCacheDao != null) {
      return _filesCacheDao;
    } else {
      synchronized(this) {
        if(_filesCacheDao == null) {
          _filesCacheDao = new FilesCacheDao_Impl(this);
        }
        return _filesCacheDao;
      }
    }
  }

  @Override
  public RecycleBinDao recycleBinDao() {
    if (_recycleBinDao != null) {
      return _recycleBinDao;
    } else {
      synchronized(this) {
        if(_recycleBinDao == null) {
          _recycleBinDao = new RecycleBinDao_Impl(this);
        }
        return _recycleBinDao;
      }
    }
  }

  @Override
  public VaultDao vaultDao() {
    if (_vaultDao != null) {
      return _vaultDao;
    } else {
      synchronized(this) {
        if(_vaultDao == null) {
          _vaultDao = new VaultDao_Impl(this);
        }
        return _vaultDao;
      }
    }
  }

  @Override
  public ScanResultDao scanResultDao() {
    if (_scanResultDao != null) {
      return _scanResultDao;
    } else {
      synchronized(this) {
        if(_scanResultDao == null) {
          _scanResultDao = new ScanResultDao_Impl(this);
        }
        return _scanResultDao;
      }
    }
  }

  @Override
  public BookmarkDao bookmarkDao() {
    if (_bookmarkDao != null) {
      return _bookmarkDao;
    } else {
      synchronized(this) {
        if(_bookmarkDao == null) {
          _bookmarkDao = new BookmarkDao_Impl(this);
        }
        return _bookmarkDao;
      }
    }
  }

  @Override
  public OperationLogDao operationLogDao() {
    if (_operationLogDao != null) {
      return _operationLogDao;
    } else {
      synchronized(this) {
        if(_operationLogDao == null) {
          _operationLogDao = new OperationLogDao_Impl(this);
        }
        return _operationLogDao;
      }
    }
  }
}
