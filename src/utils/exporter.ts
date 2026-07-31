import ExcelJS from "exceljs";

export async function buildExportBuffer(
  tab: string,
  dataRows: Record<string, unknown>[],
): Promise<Buffer> {
  const workbook = new ExcelJS.Workbook();
  workbook.creator = "library-backend";
  workbook.created = new Date();

  const sheet = workbook.addWorksheet(tab);

  const headers =
    dataRows.length > 0 ? Object.keys(dataRows[0]) : [];

  if (headers.length > 0) {
    sheet.addRow(headers);
    for (const row of dataRows) {
      sheet.addRow(headers.map((h) => row[h] ?? ""));
    }
  }

  const buffer = await workbook.xlsx.writeBuffer();
  return Buffer.from(buffer);
}
