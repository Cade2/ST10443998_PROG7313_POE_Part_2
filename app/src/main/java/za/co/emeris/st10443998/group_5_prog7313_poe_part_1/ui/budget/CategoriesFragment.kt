package za.co.emeris.st10443998.group_5_prog7313_poe_part_1.ui.budget

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.R
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.adapters.CategoryAdapter
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.entity.CategoryEntity
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.data.repository.StashRepository
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.BottomSheetCategoryBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.databinding.FragmentCategoriesBinding
import za.co.emeris.st10443998.group_5_prog7313_poe_part_1.model.Category

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    private var userId = -1
    private var currentEntities: List<CategoryEntity> = emptyList()

    private val colorOptions = listOf(
        "#4CAF50", "#2196F3", "#9C27B0", "#FF9800", "#F44336", "#607D8B"
    )

    private val defaultCategories = listOf(
        "Groceries" to "#4CAF50",
        "Transport" to "#2196F3",
        "Entertainment" to "#9C27B0",
        "Eating Out" to "#FF9800",
        "Health" to "#F44336",
        "Other" to "#607D8B"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireContext()
            .getSharedPreferences("stash_prefs", Context.MODE_PRIVATE)
            .getInt("userId", -1)

        val repository = StashRepository.getInstance(requireContext())
        viewModel = ViewModelProvider(this, CategoryViewModel.Factory(repository))[CategoryViewModel::class.java]

        adapter = CategoryAdapter(
            emptyList(),
            onEditClick = { category ->
                val entity = currentEntities.find { it.id == category.id }
                if (entity != null) showCategoryBottomSheet(entity)
            },
            onLongClick = { category ->
                val entity = currentEntities.find { it.id == category.id }
                if (entity != null) confirmDelete(entity)
            }
        )

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoriesFragment.adapter
        }

        viewModel.getCategories(userId).observe(viewLifecycleOwner) { entities ->
            currentEntities = entities
            if (entities.isEmpty()) {
                seedDefaults()
            } else {
                val display = entities.map { entity ->
                    Category(
                        id = entity.id,
                        name = entity.name,
                        colorResId = 0,
                        color = runCatching { Color.parseColor(entity.colorHex) }.getOrElse { Color.GRAY }
                    )
                }
                adapter.updateCategories(display)
            }
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.fabAddCategory.setOnClickListener { showCategoryBottomSheet(null) }
    }

    private fun seedDefaults() {
        lifecycleScope.launch(Dispatchers.Main) {
            defaultCategories.forEach { (name, hex) ->
                viewModel.addCategory(name, hex, userId)
            }
        }
    }

    private fun showCategoryBottomSheet(existing: CategoryEntity?) {
        val dialog = BottomSheetDialog(requireContext())
        val sheet = BottomSheetCategoryBinding.inflate(layoutInflater)

        var selectedHex = existing?.colorHex ?: colorOptions[0]

        if (existing != null) {
            sheet.tvTitle.text = getString(R.string.title_edit_category)
            sheet.etCategoryName.setText(existing.name)
        }

        val circles = listOf(sheet.colour1, sheet.colour2, sheet.colour3,
                             sheet.colour4, sheet.colour5, sheet.colour6)

        fun highlightSelected() {
            circles.forEachIndexed { i, v ->
                v.alpha = if (colorOptions[i] == selectedHex) 1f else 0.4f
            }
        }
        highlightSelected()

        circles.forEachIndexed { i, v ->
            v.setOnClickListener {
                selectedHex = colorOptions[i]
                highlightSelected()
            }
        }

        sheet.btnCancel.setOnClickListener { dialog.dismiss() }

        sheet.btnSave.setOnClickListener {
            val name = sheet.etCategoryName.text?.toString()?.trim() ?: ""
            if (name.isEmpty()) {
                sheet.tilCategoryName.error = getString(R.string.error_required_field)
                return@setOnClickListener
            }
            sheet.tilCategoryName.error = null

            lifecycleScope.launch(Dispatchers.Main) {
                if (existing == null) {
                    viewModel.addCategory(name, selectedHex, userId)
                } else {
                    viewModel.updateCategory(existing.copy(name = name, colorHex = selectedHex))
                }
                dialog.dismiss()
            }
        }

        dialog.setContentView(sheet.root)
        dialog.show()
    }

    private fun confirmDelete(entity: CategoryEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete category")
            .setMessage("Delete \"${entity.name}\"? All associated expenses will also be deleted.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.deleteCategory(entity)
                }
                Snackbar.make(binding.root, "\"${entity.name}\" deleted", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
