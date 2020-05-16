package ke.co.visualdiagnoser.besafe.ui.upload.model

import androidx.annotation.Keep
import ke.co.visualdiagnoser.besafe.streetpass.persistence.StreetPassRecord
@Keep
class ExportData constructor(var records: List<StreetPassRecord>)